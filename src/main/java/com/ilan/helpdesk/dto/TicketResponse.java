package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.TicketPriority;
import com.ilan.helpdesk.enums.TicketStatus;

import java.util.List;

public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private int commentsCount;
    private List<CommentResponse> comments;
    private String createdByEmail;
    private String assignedToEmail;
    private String assignedToFullName;

    public TicketResponse() {
    }
    public TicketResponse(Long id, String title, String description, TicketStatus status,
                          TicketPriority priority, int commentsCount,
                          List<CommentResponse> comments, String createdByEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.commentsCount = commentsCount;
        this.comments = comments;
        this.createdByEmail = createdByEmail;
    }
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public TicketStatus getStatus() {
        return status;
    }
    public TicketPriority getPriority() {
        return priority;
    }
    public int getCommentsCount() {
        return commentsCount;
    }
    public List<CommentResponse> getComments() {
        return comments;
    }
    public String getCreatedByEmail() {
        return createdByEmail;
    }
    public String getAssignedToEmail() {
        return assignedToEmail;
    }
    public String getAssignedToFullName() {
        return assignedToFullName;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }
    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }
    public void setAssignedToEmail(String assignedToEmail) {
        this.assignedToEmail = assignedToEmail;
    }
    public void setAssignedToFullName(String assignedToFullName) {
        this.assignedToFullName = assignedToFullName;
    }
}