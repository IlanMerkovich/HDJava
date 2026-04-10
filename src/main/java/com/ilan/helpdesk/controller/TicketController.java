package com.ilan.helpdesk.controller;

import com.ilan.helpdesk.dto.*;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RequestMapping("/api/tickets") //this means all methods start from this path//
@RestController //this says that this class handles HTTP requests//
public class TicketController{
    private final TicketService ticketService;
    // the object is created by the service, this is called dependency injection //
    public TicketController(TicketService ticketService){
        this.ticketService=ticketService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public PagedTicketResponse getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String query,
            Authentication authentication) {
        return ticketService.getAllTicketsPaged(status, priority, page, size, sortBy, direction, authentication,query);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN','AGENT')")
    public TicketStatsResponse getTicketStats(Authentication authentication){
        return ticketService.getTicketStats(authentication);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public List<TicketHistoryResponse> getTicketHistory(@PathVariable long id, Authentication authentication){
        return ticketService.getTicketHistory(id,authentication);
    }

    @GetMapping("/{id}") //returns ticket by id ==> which means when we get /api/ticket/1 , it will return the ticket. PathVariable means take the id form url
    //for example /api/ticket/999 --> id=999//
    @PreAuthorize("hasAnyRole('CLIENT','AGENT','ADMIN')")
    public TicketResponse getTicketById(@PathVariable long id,Authentication authentication){
        return ticketService.getTicketById(id,authentication);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public TicketResponse updateTicketStatus(@PathVariable long id, @Valid @RequestBody UpdateTicketStatusRequest request,Authentication authentication){
        return ticketService.updateTicketStatus(id,request,authentication);
    }

    @PostMapping //creating a new ticket
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request,
                                       Authentication authentication) /*this means that we take the json file and create and object. in addition, before we invoke
    this method we tell her to check all validation rules */ {
        return ticketService.createTicket(request,authentication);
    }
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public List<CommentResponse> getCommentsByTicketId(@PathVariable long id,Authentication authentication){
        return ticketService.getCommentsByTicketId(id,authentication);
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public CommentResponse addCommentToTicket(@PathVariable long id,@Valid @RequestBody AddCommentRequest request
    ,Authentication authentication){
        return ticketService.addCommentToTicket(id,request,authentication);
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public TicketResponse assignTicket(@PathVariable long id, @Valid @RequestBody AssignTicketRequest request, Authentication authentication){
        return ticketService.assignTicket(id,request,authentication);
    }

    @PatchMapping("/{id}/reopen")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public TicketResponse reopenTicket(@PathVariable long id,Authentication authentication){
        return ticketService.reopenTicket(id,authentication);
    }

    @PatchMapping("/{id}/unassign")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public TicketResponse unassignTicket(@PathVariable long id,Authentication authentication){
        return ticketService.unassignTicket(id,authentication);
    }



}

