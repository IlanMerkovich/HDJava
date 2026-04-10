package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTicketRequest {
    @NotNull(message = "Agent id is required")
    private long agentId;
    public AssignTicketRequest(){}
    public AssignTicketRequest(long agentId){
        this.agentId=agentId;
    }
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }
    public long getAgentId() {
        return agentId;
    }
}
