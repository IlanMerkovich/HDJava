package support;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class IntegrationTestHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final NotificationRepository notificationRepository;
    public IntegrationTestHelper(MockMvc mockMvc,
                                 ObjectMapper objectMapper,
                                 UserRepository userRepository,
                                 TicketRepository ticketRepository,
                                 CommentRepository commentRepository,
                                 TicketHistoryRepository ticketHistoryRepository,
                                 TicketAttachmentRepository ticketAttachmentRepository,
                                 NotificationRepository notificationRepository){
        this.commentRepository=commentRepository;
        this.ticketRepository=ticketRepository;
        this.notificationRepository=notificationRepository;
        this.userRepository=userRepository;
        this.ticketAttachmentRepository=ticketAttachmentRepository;
        this.ticketHistoryRepository=ticketHistoryRepository;
        this.mockMvc=mockMvc;
        this.objectMapper=objectMapper;
    }
    public void clearDatabase() {
        commentRepository.deleteAll();
        ticketAttachmentRepository.deleteAll();
        ticketHistoryRepository.deleteAll();
        notificationRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void registerUser(String fullName,String email,String password)throws Exception{
        RegisterRequest registerRequest=new RegisterRequest();
        registerRequest.setFullName(fullName);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))).andExpect(status().isOk());
    }
    public String loginAndGetToken(String email,String password)throws Exception{
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        String response=mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
    public void updateUserRole(String email,Role role){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found: "+email));
        user.setRole(role);
        userRepository.save(user);
    }
    public Long getUserIdByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user with email: "+email+" not found")).getId();
    }
    public Long createTicketAndGetId(String token, String requestBody) throws Exception {
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
    public void assignTicket(String token, Long ticketId, Long agentId) throws Exception {
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
    public String registerAndLoginUser(String fullName, String email, String password) throws Exception {
        registerUser(fullName, email, password);
        return loginAndGetToken(email, password);
    }
}
