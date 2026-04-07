package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class createTicketRequest {

    @NotBlank(message = "Title is required") //title must not be empty//
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") //title must be in size//
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    public createTicketRequest() {
    }
    public createTicketRequest(String title, String description,TicketPriority priority) {
        this.title = title;
        this.description = description;
        this.priority=priority;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TicketPriority getPriority() {
        return priority;
    }
    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
}