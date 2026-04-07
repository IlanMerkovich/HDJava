package com.ilan.helpdesk.controller;

import com.ilan.helpdesk.dto.*;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.model.Comment;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public List<TicketResponse> getAllTickets(@RequestParam(required = false) TicketStatus status, @RequestParam(required = false) TicketPriority priority){
        return ticketService.getAllTickets(status, priority);
    }

    @GetMapping("/{id}") //returns ticket by id ==> which means when we get /api/ticket/1 , it will return the ticket. PathVariable means take the id form url
    //for example /api/ticket/999 --> id=999//
    public TicketResponse getTicketById(@PathVariable long id){
        return ticketService.getTicketById(id);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateTicketStatus(@PathVariable long id, @Valid @RequestBody UpdateTicketStatusRequest request){
        return ticketService.updateTicketStatus(id,request);
    }

    @PostMapping //creating a new ticket
    public TicketResponse createTicket(@Valid @RequestBody createTicketRequest request) /*this means that we take the json file and create and object. in addition, before we invoke
    this method we tell her to check all validation rules */ {
        return ticketService.createTicket(request);
    }
    @GetMapping("/{id}/comments")
    public List<CommentResponse> getCommentsByTicketId(@PathVariable long id){
        return ticketService.getCommentsByTicketId(id);
    }

    @PostMapping("/{id}/comments")
    public CommentResponse addCommentToTicket(@PathVariable long id,@Valid @RequestBody addCommentRequest request){
        return ticketService.addCommentToTicket(id,request);
    }



}

