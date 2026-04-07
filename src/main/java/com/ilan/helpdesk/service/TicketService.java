package com.ilan.helpdesk.service;

import com.ilan.helpdesk.dto.*;
import com.ilan.helpdesk.enums.Role;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.exception.InvalidTicketStateException;
import com.ilan.helpdesk.exception.InvalidUserRoleException;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import com.ilan.helpdesk.model.Comment;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.User;
import com.ilan.helpdesk.repository.CommentRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import com.ilan.helpdesk.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TicketResponse assignTicket(long tickedId, assignTicketRequest request){
        Ticket ticket=findTicketEntityById(tickedId);
        User agent=userRepository.findById(request.getAgentId()).orElseThrow(()->new ResourceNotFoundException("agent with id "+request.getAgentId()+" not found"));

        if (agent.getRole()!=Role.AGENT){
            throw new InvalidUserRoleException("cant assign ticket to a non-agent user");
        }
        ticket.setAssignedTo(agent);
        Ticket updatedTicket=ticketRepository.save(ticket);
        return mapTicketToResponse(updatedTicket);
    }
    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository,UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository=userRepository;
    }
    public List<TicketResponse> getAllTickets(TicketStatus status, TicketPriority priority, Authentication authentication) {
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Ticket> tickets;

        if (currentUser.getRole() == Role.CLIENT) {
            if (status != null && priority != null) {
                tickets = ticketRepository.findByCreatedByAndStatusAndPriority(currentUser, status, priority);
            } else if (status != null) {
                tickets = ticketRepository.findByCreatedByAndStatus(currentUser, status);
            } else if (priority != null) {
                tickets = ticketRepository.findByCreatedByAndPriority(currentUser, priority);
            } else {
                tickets = ticketRepository.findByCreatedBy(currentUser);
            }
        } else {
            if (status != null && priority != null) {
                tickets = ticketRepository.findByStatusAndPriority(status, priority);
            } else if (status != null) {
                tickets = ticketRepository.findByStatus(status);
            } else if (priority != null) {
                tickets = ticketRepository.findByPriority(priority);
            } else {
                tickets = ticketRepository.findAll();
            }
        }

        List<TicketResponse> responses = new ArrayList<>();
        for (Ticket ticket : tickets) {
            responses.add(mapTicketToResponse(ticket));
        }

        return responses;
    }
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = findTicketEntityById(id);
        return mapTicketToResponse(ticket);
    }
    public TicketResponse createTicket(createTicketRequest request, Authentication authentication) {
        String email=authentication.getName();
        User user=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));

        Ticket ticket = new Ticket();
        ticket.setCreatedBy(user);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(request.getPriority());

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapTicketToResponse(savedTicket);
    }
    public TicketResponse updateTicketStatus(Long id, UpdateTicketStatusRequest request) {
        Ticket ticket = findTicketEntityById(id);
        TicketStatus newStatus = request.getStatus();

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new InvalidTicketStateException("Cannot update a closed ticket");
        }

        if (newStatus == TicketStatus.CLOSED && ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new InvalidTicketStateException("Ticket can be closed only after it is resolved");
        }

        ticket.setStatus(newStatus);
        Ticket updatedTicket = ticketRepository.save(ticket);

        return mapTicketToResponse(updatedTicket);
    }
    public List<CommentResponse> getCommentsByTicketId(Long ticketId) {
        findTicketEntityById(ticketId);

        List<Comment> comments = commentRepository.findByTicketId(ticketId);
        List<CommentResponse> responses = new ArrayList<>();

        for (Comment comment : comments) {
            responses.add(mapCommentToResponse(comment));
        }

        return responses;
    }
    public CommentResponse addCommentToTicket(Long ticketId, addCommentRequest request,Authentication authentication) {
        Ticket ticket = findTicketEntityById(ticketId);
        String email=authentication.getName();
        User currUser=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));

        Comment comment = new Comment();

        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTicket(ticket);
        comment.setAuthor(currUser);

        Comment savedComment = commentRepository.save(comment);
        return mapCommentToResponse(savedComment);
    }
    private Ticket findTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"));
    }
    private TicketResponse mapTicketToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());

        if (ticket.getCreatedBy() != null) {
            response.setCreatedByEmail(ticket.getCreatedBy().getEmail());
        }
        if (ticket.getAssignedTo()!=null){
            response.setAssignedToFullName(ticket.getAssignedTo().getFullName());
            response.setAssignedToEmail(ticket.getAssignedTo().getEmail());
        }

        response.setCommentsCount(0);
        response.setComments(new ArrayList<>());

        return response;
    }
    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor()!=null){
            response.setAuthorEmail(comment.getAuthor().getEmail());
            response.setAuthorName(comment.getAuthor().getFullName());
        }
        return response;
    }
}