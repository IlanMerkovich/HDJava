package com.ilan.helpdesk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String authorName;
    @Column(nullable = false,length = 300)
    private String content;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY) //many to one, 1 ticket with many comments
    @JoinColumn(name = "ticket_id",nullable = false) //foreign key from ticket table
    @JsonBackReference
    private Ticket ticket;

    public Comment() {
    }
    public Comment(Long id, String authorName, String content, LocalDateTime createdAt,Ticket ticket) {
        this.id = id;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
        this.ticket=ticket;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Ticket getTicket(){
        return ticket;
    }
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}