package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {
    private long id;
    public String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private long relatedTicket;

    public NotificationResponse(){};

    public NotificationType getType() {
        return type;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getMessage() {
        return message;
    }
    public long getId() {
        return id;
    }
    public long getRelatedTicket() {
        return relatedTicket;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRelatedTicket(long relatedTicket) {
        this.relatedTicket = relatedTicket;
    }
}
