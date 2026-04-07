package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketStatusRequest {
    @NotNull(message = "Status is required")
    private TicketStatus status;
    UpdateTicketStatusRequest(){

    }
    public UpdateTicketStatusRequest(TicketStatus status){
        this.status=status;
    }
    public TicketStatus getStatus() {
        return status;
    }
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
