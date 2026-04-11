package controller;

import com.ilan.helpdesk.dto.LoginRequest;
import com.ilan.helpdesk.dto.RegisterRequest;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.CommentRepository;
import com.ilan.helpdesk.repository.TicketAttachmentRepository;
import com.ilan.helpdesk.repository.TicketHistoryRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import com.ilan.helpdesk.repository.UserRepository;
import com.ilan.helpdesk.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import support.IntegrationTestHelper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.ilan.helpdesk.HelpdeskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketLifecycleIntegrationTest {

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
    void ticketLifecycle_shouldWorkEndToEnd() throws Exception {
        String clientEmail = "client_lifecycle@example.com";
        String adminEmail = "admin_lifecycle@example.com";
        String agentEmail = "agent_lifecycle@example.com";
        String password = "123456";

        helper.registerUser("Lifecycle Client", clientEmail, password);
        helper.registerUser("Lifecycle Admin", adminEmail, password);
        helper.registerUser("Lifecycle Agent", agentEmail, password);

        helper.updateUserRole(adminEmail, Role.ADMIN);
        helper.updateUserRole(agentEmail, Role.AGENT);

        String clientToken = helper.loginAndGetToken(clientEmail, password);
        String adminToken = helper.loginAndGetToken(adminEmail, password);
        String agentToken = helper.loginAndGetToken(agentEmail, password);

        Long agentId = helper.getUserIdByEmail(agentEmail);

        Long ticketId = helper.createTicketAndGetId(clientToken,
                """
                {
                  "title": "Lifecycle test ticket",
                  "description": "Testing full lifecycle flow",
                  "priority": "HIGH"
                }
                """);

        helper.assignTicket(adminToken, ticketId, agentId);
        updateStatus(agentToken, ticketId, "IN_PROGRESS");
        updateStatus(agentToken, ticketId, "RESOLVED");
        updateStatus(agentToken, ticketId, "CLOSED");
        reopenTicket(clientToken, ticketId);

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedToEmail").value(agentEmail))
                .andExpect(jsonPath("$.createdByEmail").value(clientEmail));

        String historyResponse = mockMvc.perform(get("/api/tickets/" + ticketId + "/history")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionType").value("ASSIGNED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode history = objectMapper.readTree(historyResponse);

        assertTrue(history.isArray(), "History response should be an array");
        assertTrue(history.size() >= 5, "History should contain assignment and status changes");

        boolean hasAssigned = false;
        boolean hasInProgress = false;
        boolean hasResolved = false;
        boolean hasClosed = false;
        boolean hasReopenToOpen = false;

        for (JsonNode item : history) {
            String actionType = item.path("actionType").asText();

            if ("ASSIGNED".equals(actionType)) {
                hasAssigned = true;
            }

            String newStatus = item.path("newStatus").isMissingNode() ? null : item.path("newStatus").asText(null);
            String oldStatus = item.path("oldStatus").isMissingNode() ? null : item.path("oldStatus").asText(null);

            if ("IN_PROGRESS".equals(newStatus)) {
                hasInProgress = true;
            }
            if ("RESOLVED".equals(newStatus)) {
                hasResolved = true;
            }
            if ("CLOSED".equals(newStatus)) {
                hasClosed = true;
            }
            if ("CLOSED".equals(oldStatus) && "OPEN".equals(newStatus)) {
                hasReopenToOpen = true;
            }
        }

        assertTrue(hasAssigned, "History should contain ASSIGNED action");
        assertTrue(hasInProgress, "History should contain IN_PROGRESS change");
        assertTrue(hasResolved, "History should contain RESOLVED change");
        assertTrue(hasClosed, "History should contain CLOSED change");
        assertTrue(hasReopenToOpen, "History should contain reopen from CLOSED to OPEN");
    }

    private void updateStatus(String token, Long ticketId, String status) throws Exception {
        String requestBody = """
                {
                  "status": "%s"
                }
                """.formatted(status);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(status));
    }

    private void reopenTicket(String token, Long ticketId) throws Exception {
        mockMvc.perform(patch("/api/tickets/" + ticketId + "/reopen")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }
}