package controller;

import com.ilan.helpdesk.dto.LoginRequest;
import com.ilan.helpdesk.dto.RegisterRequest;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.model.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.ilan.helpdesk.HelpdeskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketAccessControlIntegrationTest {

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

    @Test
    void ticketAccessControl_shouldRestrictUsersCorrectly() throws Exception {
        String clientAEmail = "clientA_access@example.com";
        String clientBEmail = "clientB_access@example.com";
        String adminEmail = "admin_access@example.com";
        String agentEmail = "agent_access@example.com";
        String password = "123456";

        helper.registerUser("Client A", clientAEmail, password);
        helper.registerUser("Client B", clientBEmail, password);
        helper.registerUser("Admin User", adminEmail, password);
        helper.registerUser("Agent User", agentEmail, password);

        helper.updateUserRole(adminEmail, Role.ADMIN);
        helper.updateUserRole(agentEmail, Role.AGENT);

        String clientAToken = helper.loginAndGetToken(clientAEmail, password);
        String clientBToken = helper.loginAndGetToken(clientBEmail, password);
        String adminToken = helper.loginAndGetToken(adminEmail, password);
        String agentToken = helper.loginAndGetToken(agentEmail, password);

        Long agentId = helper.getUserIdByEmail(agentEmail);

        Long ticketId = helper.createTicketAndGetId(clientAToken,
                """
                {
                  "title": "Access control test",
                  "description": "Checking who can access this ticket",
                  "priority": "HIGH"
                }
                """);

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + clientAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdByEmail").value(clientAEmail));

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + clientBToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + agentToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdByEmail").value(clientAEmail));

        helper.assignTicket(adminToken, ticketId, agentId);

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + agentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedToEmail").value(agentEmail));

        mockMvc.perform(get("/api/tickets/" + ticketId + "/comments")
                        .header("Authorization", "Bearer " + clientBToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tickets/" + ticketId + "/history")
                        .header("Authorization", "Bearer " + clientBToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tickets/" + ticketId + "/history")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tickets/" + ticketId + "/history")
                        .header("Authorization", "Bearer " + agentToken))
                .andExpect(status().isOk());
    }
}