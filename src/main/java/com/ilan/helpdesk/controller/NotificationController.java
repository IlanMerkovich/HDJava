package com.ilan.helpdesk.controller;


import com.ilan.helpdesk.dto.NotificationResponse;
import com.ilan.helpdesk.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService){
        this.notificationService=notificationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('AMDIN','CLIENT','AGENT')")
    public List<NotificationResponse> getMyNotifications(Authentication authentication){
        return notificationService.getMyResponses(authentication);
    }
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public NotificationResponse markAsRead(@PathVariable Long id, Authentication authentication) {
        return notificationService.markAsRead(id, authentication);
    }
    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public void markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication);
    }
}
