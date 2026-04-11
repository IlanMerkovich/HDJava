package controller;

import com.ilan.helpdesk.repository.CommentRepository;
import com.ilan.helpdesk.repository.NotificationRepository;
import com.ilan.helpdesk.repository.TicketAttachmentRepository;
import com.ilan.helpdesk.repository.TicketHistoryRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import com.ilan.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import support.IntegrationTestHelper;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.ilan.helpdesk.HelpdeskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    @Autowired
    private TicketAttachmentRepository ticketAttachmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private IntegrationTestHelper helper;

    @BeforeEach
    void setUp() {
        helper = new IntegrationTestHelper(
                mockMvc,
                objectMapper,
                userRepository,
                ticketRepository,
                commentRepository,
                ticketHistoryRepository,
                ticketAttachmentRepository,
                notificationRepository
        );
        helper.clearDatabase();
    }

    private String registerAndLoginClient() throws Exception {
        String fullName = "Ticket Test User";
        String email = "ticketuser@example.com";
        String password = "123456";

        helper.registerUser(fullName, email, password);
        return helper.loginAndGetToken(email, password);
    }

    @Test
    void createTicket_shouldFailWhenPriorityIsMissing() throws Exception {
        String token = registerAndLoginClient();

        String requestBody = """
                {
                  "title": "Printer issue",
                  "description": "The printer is not working"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.priority").exists());
    }

    @Test
    void createTicket_shouldFailWhenTitleIsBlank() throws Exception {
        String token = registerAndLoginClient();

        String requestBody = """
                {
                  "title": "",
                  "description": "The printer is not working",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void addComment_shouldFailWhenContentIsBlank() throws Exception {
        String token = registerAndLoginClient();

        Long ticketId = helper.createTicketAndGetId(token, """
                {
                  "title": "Comment test ticket",
                  "description": "Testing comment validation",
                  "priority": "HIGH"
                }
                """);

        String commentBody = """
                {
                  "content": ""
                }
                """;

        mockMvc.perform(post("/api/tickets/" + ticketId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    void updateStatus_shouldFailWhenStatusIsMissing() throws Exception {
        String token = registerAndLoginClient();

        Long ticketId = helper.createTicketAndGetId(token, """
                {
                  "title": "Status test ticket",
                  "description": "Testing status validation",
                  "priority": "HIGH"
                }
                """);

        String statusBody = """
                {
                }
                """;

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.status").exists());
    }

    @Test
    void assignTicket_shouldFailWhenAgentIdIsMissing() throws Exception {
        String token = registerAndLoginClient();

        Long ticketId = helper.createTicketAndGetId(token, """
                {
                  "title": "Assign test ticket",
                  "description": "Testing assign validation",
                  "priority": "HIGH"
                }
                """);

        String assignBody = """
                {
                }
                """;

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/assign")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.agentId").exists());
    }
}