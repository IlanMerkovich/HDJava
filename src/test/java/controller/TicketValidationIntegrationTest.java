package controller;

import com.ilan.helpdesk.dto.LoginRequest;
import com.ilan.helpdesk.dto.RegisterRequest;
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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
    void clearDatabase() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }



    private String registerAndLoginClient() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Ticket Test User");
        registerRequest.setEmail("ticketuser@example.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("ticketuser@example.com");
        loginRequest.setPassword("123456");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
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

        String createTicketBody = """
                {
                  "title": "Comment test ticket",
                  "description": "Testing comment validation",
                  "priority": "HIGH"
                }
                """;

        String ticketResponse = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTicketBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ticketId = objectMapper.readTree(ticketResponse).get("id").asLong();

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

        String createTicketBody = """
                {
                  "title": "Status test ticket",
                  "description": "Testing status validation",
                  "priority": "HIGH"
                }
                """;

        String ticketResponse = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTicketBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ticketId = objectMapper.readTree(ticketResponse).get("id").asLong();

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

        String createTicketBody = """
                {
                  "title": "Assign test ticket",
                  "description": "Testing assign validation",
                  "priority": "HIGH"
                }
                """;

        String ticketResponse = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTicketBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ticketId = objectMapper.readTree(ticketResponse).get("id").asLong();

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