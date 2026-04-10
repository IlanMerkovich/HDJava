package com.ilan.helpdesk.controller;
import com.ilan.helpdesk.dto.AuthResponse;
import com.ilan.helpdesk.dto.LoginRequest;
import com.ilan.helpdesk.dto.RegisterRequest;
import com.ilan.helpdesk.dto.UserResponse;
import com.ilan.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.ilan.helpdesk.dto.CurrentUserResponse;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication){
        return userService.getCurrentUser(authentication);
    }


}