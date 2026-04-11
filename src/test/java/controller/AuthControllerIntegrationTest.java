package controller;

import com.ilan.helpdesk.dto.LoginRequest;
import com.ilan.helpdesk.dto.RegisterRequest;
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
import support.IntegrationTestHelper;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.ilan.helpdesk.HelpdeskApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    private IntegrationTestHelper helper;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("testuser@example.com");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    void login_shouldReturnTokenForValidCredentials() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Login User");
        registerRequest.setEmail("loginuser@example.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("loginuser@example.com");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fullName").value("Login User"))
                .andExpect(jsonPath("$.email").value("loginuser@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void login_shouldFailForWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Wrong Password User");
        registerRequest.setEmail("wrongpassword@example.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrongpassword@example.com");
        loginRequest.setPassword("654321");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));


    }
    @Test
    void register_shouldFailWhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("not-an-email");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void login_shouldFailWhenEmailIsBlank() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void login_shouldFailWhenEmailFormatIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("abc");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }
}