package com.ilan.helpdesk.controller;

import com.ilan.helpdesk.dto.userResponse;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<userResponse> getAllUsers(){
        return userService.getAllUsers();
    }
}
