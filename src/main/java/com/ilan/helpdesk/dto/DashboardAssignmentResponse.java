package com.ilan.helpdesk.dto;

public class DashboardAssignmentResponse {
    private long assignedTickets;
    private long unassignedTickets;
    public DashboardAssignmentResponse(){}
    public long getUnassignedTickets() {
        return unassignedTickets;
    }
    public long getAssignedTickets() {
        return assignedTickets;
    }
    public void setUnassignedTickets(long unassignedTickets) {
        this.unassignedTickets = unassignedTickets;
    }
    public void setAssignedTickets(long assignedTickets) {
        this.assignedTickets = assignedTickets;
    }
}
