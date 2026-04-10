package com.ilan.helpdesk.dto;

public class AgentWorkloadResponse {
    private long agentId;
    private String agentName;
    private String agentEmail;
    private long assignedTickets;
    private long openedTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long highPriorityTickets;
    public AgentWorkloadResponse(){}

    public long getAssignedTickets() {
        return assignedTickets;
    }
    public long getHighPriorityTickets() {
        return highPriorityTickets;
    }
    public long getInProgressTickets() {
        return inProgressTickets;
    }
    public long getResolvedTickets() {
        return resolvedTickets;
    }
    public long getClosedTickets() {
        return closedTickets;
    }
    public long getAgentId() {
        return agentId;
    }
    public long getOpenedTickets() {
        return openedTickets;
    }
    public String getAgentEmail() {
        return agentEmail;
    }
    public String getAgentName() {
        return agentName;
    }
    public void setAssignedTickets(long assignedTickets) {
        this.assignedTickets = assignedTickets;
    }
    public void setHighPriorityTickets(long highPriorityTickets) {
        this.highPriorityTickets = highPriorityTickets;
    }
    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }
    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }
    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }
    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
    public void setOpenedTickets(long openedTickets) {
        this.openedTickets = openedTickets;
    }
}
