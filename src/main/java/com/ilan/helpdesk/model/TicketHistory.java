package com.ilan.helpdesk.model;


import com.ilan.helpdesk.enums.TicketHistoryActionType;
import com.ilan.helpdesk.enums.TicketStatus;
import jakarta.persistence.*;
import jdk.jshell.Snippet;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id",nullable = false)
    private Ticket ticket;
    @Enumerated(EnumType.STRING)
    private TicketHistoryActionType ActionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private TicketStatus oldStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private TicketStatus newStatus;
    @Column(name = "old_assigned_to_email")
    private String oldAssignedToEmail;
    @Column(name = "new_assigned_to_email")
    private String newAssignedToEmail;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedBy;
    @Column(nullable = false)
    private LocalDateTime changedAt;
    public TicketHistory(){}

    public long getId() {
        return id;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getNewAssignedToEmail() {
        return newAssignedToEmail;
    }

    public String getOldAssignedToEmail() {
        return oldAssignedToEmail;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public TicketHistoryActionType getActionType() {
        return ActionType;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setActionType(TicketHistoryActionType actionType) {
        ActionType = actionType;
    }

    public void setNewAssignedToEmail(String newAssignedToEmail) {
        this.newAssignedToEmail = newAssignedToEmail;
    }

    public void setNewStatus(TicketStatus newStatus) {
        this.newStatus = newStatus;
    }

    public void setOldAssignedToEmail(String oldAssignedToEmail) {
        this.oldAssignedToEmail = oldAssignedToEmail;
    }

    public void setOldStatus(TicketStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

}
