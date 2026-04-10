package com.ilan.helpdesk.service;

import com.ilan.helpdesk.dto.*;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.exception.EmailAlreadyExistsException;
import com.ilan.helpdesk.exception.InvalidCredentialsException;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ilan.helpdesk.dto.CurrentUserResponse;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CLIENT);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());

        return response;
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;
    }
    public CurrentUserResponse getCurrentUser(Authentication authentication){
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));
        CurrentUserResponse response=new CurrentUserResponse();
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }
    public UserResponse mapToUserResponse(User user){
        UserResponse response=new UserResponse();

        response.setEmail(user.getEmail());
        response.setId(user.getId());
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());
        return response;

    }
    public List<UserResponse> getAllUsers(){
        List<User>users=userRepository.findAll();
        List<UserResponse>userResponses=new ArrayList<>();
        for (User user:users){
            userResponses.add(mapToUserResponse(user));
        }
        return userResponses;
    }
}