package com.ilan.helpdesk.model;

import com.ilan.helpdesk.enums.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead;

    @Column
    private LocalDateTime createdAt;

    @Column
    private long relatedTicketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id",nullable = false)
    private User recipient;

    public Notification(){}

    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();
        this.isRead=false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getRelatedTicketId() {
        return relatedTicketId;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setRelatedTicketId(long relatedTicketId) {
        this.relatedTicketId = relatedTicketId;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
