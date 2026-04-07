package com.ilan.helpdesk.repository;

import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;
import com.ilan.helpdesk.model.Ticket;
import com.ilan.helpdesk.model.User;
import jdk.jshell.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByPriority(TicketPriority priority);
    List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByCreatedByAndStatus(User user, TicketStatus status);
    List<Ticket> findByCreatedByAndStatusAndPriority(User user,TicketStatus status,TicketPriority priority);
    List<Ticket> findByCreatedByAndPriority(User currUser, TicketPriority priority);
    List<Ticket> findByAssignedTo(User user);
    List<Ticket> findByAssignedToAndStatus(User user, TicketStatus status);
    List<Ticket> findByAssignedToAndPriority(User user,TicketPriority priority);
    List<Ticket> findByAssignedToAndStatusAndPriority(User user, TicketStatus status, TicketPriority priority);
}