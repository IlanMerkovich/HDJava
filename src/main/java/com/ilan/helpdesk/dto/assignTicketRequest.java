package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class assignTicketRequest {
    @NotNull(message = "Agent id is required")
    private long agentId;
    public assignTicketRequest(){}
    public assignTicketRequest(long agentId){
        this.agentId=agentId;
    }
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }
    public long getAgentId() {
        return agentId;
    }
}
