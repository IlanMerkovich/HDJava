package com.ilan.helpdesk.controller;
import com.ilan.helpdesk.dto.authResponse;
import com.ilan.helpdesk.dto.loginRequest;
import com.ilan.helpdesk.dto.registerRequest;
import com.ilan.helpdesk.dto.userResponse;
import com.ilan.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
}