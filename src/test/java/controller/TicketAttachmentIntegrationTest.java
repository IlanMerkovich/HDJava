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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import support.IntegrationTestHelper;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.ilan.helpdesk.HelpdeskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketAttachmentIntegrationTest {

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
    void attachmentFlow_shouldWorkEndToEnd() throws Exception {
        String email = "attachmentuser@example.com";
        String password = "123456";

        helper.registerUser("Attachment User", email, password);
        String token = helper.loginAndGetToken(email, password);

        Long ticketId = helper.createTicketAndGetId(token, """
                {
                  "title": "Attachment flow ticket",
                  "description": "Testing attachment flow",
                  "priority": "HIGH"
                }
                """);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "hello attachment".getBytes()
        );

        String uploadResponse = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/tickets/" + ticketId + "/attachments")
                                .file(file)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.originalFileName").value("sample.txt"))
                .andExpect(jsonPath("$.contentType").value("text/plain"))
                .andExpect(jsonPath("$.previewable").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long attachmentId = objectMapper.readTree(uploadResponse).get("id").asLong();

        mockMvc.perform(get("/api/tickets/" + ticketId + "/attachments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(attachmentId))
                .andExpect(jsonPath("$[0].originalFileName").value("sample.txt"))
                .andExpect(jsonPath("$[0].uploadedByEmail").value(email));

        mockMvc.perform(get("/api/attachments/" + attachmentId + "/download")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/attachments/" + attachmentId + "/preview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/attachments/" + attachmentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tickets/" + ticketId + "/attachments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}