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

    @BeforeEach
    void clearDatabase() {
        commentRepository.deleteAll();
        ticketAttachmentRepository.deleteAll();
        ticketHistoryRepository.deleteAll();
        notificationRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void ticketLifecycle_shouldWorkEndToEnd() throws Exception {
        String clientEmail = "client_lifecycle@example.com";
        String adminEmail = "admin_lifecycle@example.com";
        String agentEmail = "agent_lifecycle@example.com";
        String password = "123456";

        registerUser("Lifecycle Client", clientEmail, password);
        registerUser("Lifecycle Admin", adminEmail, password);
        registerUser("Lifecycle Agent", agentEmail, password);

        updateUserRole(adminEmail, Role.ADMIN);
        updateUserRole(agentEmail, Role.AGENT);

        String clientToken = loginAndGetToken(clientEmail, password);
        String adminToken = loginAndGetToken(adminEmail, password);
        String agentToken = loginAndGetToken(agentEmail, password);

        Long agentId = getUserIdByEmail(agentEmail);

        Long ticketId = createTicketAndGetId(clientToken,
                """
                {
                  "title": "Lifecycle test ticket",
                  "description": "Testing full lifecycle flow",
                  "priority": "HIGH"
                }
                """);

        assignTicket(adminToken, ticketId, agentId);
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

    private void registerUser(String fullName, String email, String password) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPassword(password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private void updateUserRole(String email, Role role) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        user.setRole(role);
        userRepository.save(user);
    }

    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email))
                .getId();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    private Long createTicketAndGetId(String token, String requestBody) throws Exception {
        String response = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private void assignTicket(String token, Long ticketId, Long agentId) throws Exception {
        String requestBody = """
                {
                  "agentId": %d
                }
                """.formatted(agentId);

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/assign")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedToEmail").exists());
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