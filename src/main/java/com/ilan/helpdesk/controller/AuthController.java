package com.ilan.helpdesk.controller;
import com.ilan.helpdesk.dto.authResponse;
import com.ilan.helpdesk.dto.loginRequest;
import com.ilan.helpdesk.dto.registerRequest;
import com.ilan.helpdesk.dto.userResponse;
import com.ilan.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.ilan.helpdesk.dto.currentUserResponse;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public userResponse register(@Valid @RequestBody registerRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public authResponse login(@Valid @RequestBody loginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public currentUserResponse me(Authentication authentication){
        return userService.getCurrentUser(authentication);
    }


}