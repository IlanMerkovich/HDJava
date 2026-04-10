package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.TicketHistoryActionType;
import com.ilan.helpdesk.enums.TicketStatus;

import java.time.LocalDateTime;

public class TicketHistoryResponse {

    private Long id;
    private TicketHistoryActionType actionType;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private String oldAssignedToEmail;
    private String newAssignedToEmail;
    private String changedByEmail;
    private String changedByFullName;
    private LocalDateTime changedAt;

    public TicketHistoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public TicketHistoryActionType getActionType() {
        return actionType;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public String getOldAssignedToEmail() {
        return oldAssignedToEmail;
    }

    public String getNewAssignedToEmail() {
        return newAssignedToEmail;
    }

    public String getChangedByEmail() {
        return changedByEmail;
    }

    public String getChangedByFullName() {
        return changedByFullName;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActionType(TicketHistoryActionType actionType) {
        this.actionType = actionType;
    }

    public void setOldStatus(TicketStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public void setNewStatus(TicketStatus newStatus) {
        this.newStatus = newStatus;
    }

    public void setOldAssignedToEmail(String oldAssignedToEmail) {
        this.oldAssignedToEmail = oldAssignedToEmail;
    }

    public void setNewAssignedToEmail(String newAssignedToEmail) {
        this.newAssignedToEmail = newAssignedToEmail;
    }

    public void setChangedByEmail(String changedByEmail) {
        this.changedByEmail = changedByEmail;
    }

    public void setChangedByFullName(String changedByFullName) {
        this.changedByFullName = changedByFullName;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}