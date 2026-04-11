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
    void ticketAccessControl_shouldRestrictUsersCorrectly() throws Exception {
        String clientAEmail = "clientA_access@example.com";
        String clientBEmail = "clientB_access@example.com";
        String adminEmail = "admin_access@example.com";
        String agentEmail = "agent_access@example.com";
        String password = "123456";

        registerUser("Client A", clientAEmail, password);
        registerUser("Client B", clientBEmail, password);
        registerUser("Admin User", adminEmail, password);
        registerUser("Agent User", agentEmail, password);

        updateUserRole(adminEmail, Role.ADMIN);
        updateUserRole(agentEmail, Role.AGENT);

        String clientAToken = loginAndGetToken(clientAEmail, password);
        String clientBToken = loginAndGetToken(clientBEmail, password);
        String adminToken = loginAndGetToken(adminEmail, password);
        String agentToken = loginAndGetToken(agentEmail, password);

        Long agentId = getUserIdByEmail(agentEmail);

        Long ticketId = createTicketAndGetId(clientAToken,
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

        assignTicket(adminToken, ticketId, agentId);

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
                .andExpect(status().isOk());
    }
}