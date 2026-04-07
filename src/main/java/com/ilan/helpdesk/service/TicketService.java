package com.ilan.helpdesk.service;

import com.ilan.helpdesk.dto.addCommentRequest;
import com.ilan.helpdesk.dto.CommentResponse;
import com.ilan.helpdesk.dto.createTicketRequest;
import com.ilan.helpdesk.dto.TicketResponse;
import com.ilan.helpdesk.dto.UpdateTicketStatusRequest;
import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.exception.InvalidTicketStateException;
import com.ilan.helpdesk.exception.ResourceNotFoundException;
import com.ilan.helpdesk.model.Comment;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.repository.CommentRepository;
import com.ilan.helpdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    public List<TicketResponse> getAllTickets(TicketStatus status, TicketPriority priority) {
        List<Ticket> tickets;

        if (status != null && priority != null) {
            tickets = ticketRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else if (priority != null) {
            tickets = ticketRepository.findByPriority(priority);
        } else {
            tickets = ticketRepository.findAll();
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

    public TicketResponse createTicket(createTicketRequest request) {
        Ticket ticket = new Ticket();
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

    public CommentResponse addCommentToTicket(Long ticketId, addCommentRequest request) {
        Ticket ticket = findTicketEntityById(ticketId);

        Comment comment = new Comment();
        comment.setAuthorName(request.getAuthorName());
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTicket(ticket);

        Comment savedComment = commentRepository.save(comment);
        return mapCommentToResponse(savedComment);
    }

    private Ticket findTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"));
    }

    private TicketResponse mapTicketToResponse(Ticket ticket) {
        List<CommentResponse> commentResponses = new ArrayList<>();

        if (ticket.getComments() != null) {
            for (Comment comment : ticket.getComments()) {
                commentResponses.add(mapCommentToResponse(comment));
            }
        }

        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCommentsCount(commentResponses.size());
        response.setComments(commentResponses);

        return response;
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setAuthorName(comment.getAuthorName());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }
}