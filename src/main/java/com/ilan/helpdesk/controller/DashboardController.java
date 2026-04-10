package com.ilan.helpdesk.controller;


import com.ilan.helpdesk.dto.DashboardSummaryResponse;
import com.ilan.helpdesk.service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final TicketService ticketService;
    public DashboardController(TicketService ticketService){
        this.ticketService=ticketService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','AGENT')")
    public DashboardSummaryResponse getDashboardSummaryResponse(Authentication authentication){
        return ticketService.getDashboardSummary(authentication);
    }
}
