package com.ilan.helpdesk.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTicketRequest {
    @NotNull(message = "Agent id is required")
    private Long agentId;
    public AssignTicketRequest(){}
    public AssignTicketRequest(Long agentId){
        this.agentId=agentId;
    }
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    public long getAgentId() {
        return agentId;
    }
}
