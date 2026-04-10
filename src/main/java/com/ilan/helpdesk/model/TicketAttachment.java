package com.ilan.helpdesk.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_attachments")
public class TicketAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false,unique = true)
    private String storedFileName;

    @Column
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id",nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id",nullable = false)
    private User createdBy;

    public TicketAttachment(){};
    @PrePersist
    public void prePersist(){
        this.createdAt=LocalDateTime.now();

    }

    public Ticket getTicket() {
        return ticket;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

}
