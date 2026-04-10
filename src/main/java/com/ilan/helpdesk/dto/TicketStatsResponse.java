package com.ilan.helpdesk.dto;

public class TicketStatsResponse {
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long highPriorityTickets;
    private long mediumPriorityTickets;
    private long lowPriorityTickets;
    private long assignedTickets;
    private long unassignedTickets;
    public TicketStatsResponse(){}
    public long getAssignedTickets() {
        return assignedTickets;
    }
    public long getClosedTickets() {
        return closedTickets;
    }
    public long getHighPriorityTickets() {
        return highPriorityTickets;
    }
    public long getInProgressTickets() {
        return inProgressTickets;
    }
    public long getLowPriorityTickets() {
        return lowPriorityTickets;
    }
    public long getMediumPriorityTickets() {
        return mediumPriorityTickets;
    }
    public long getOpenTickets() {
        return openTickets;
    }
    public long getResolvedTickets() {
        return resolvedTickets;
    }
    public long getTotalTickets() {
        return totalTickets;
    }
    public long getUnassignedTickets() {
        return unassignedTickets;
    }
    public void setAssignedTickets(long assignedTickets) {
        this.assignedTickets = assignedTickets;
    }
    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }
    public void setHighPriorityTickets(long highPriorityTickets) {
        this.highPriorityTickets = highPriorityTickets;
    }
    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }
    public void setLowPriorityTickets(long lowPriorityTickets) {
        this.lowPriorityTickets = lowPriorityTickets;
    }
    public void setMediumPriorityTickets(long mediumPriorityTickets) {
        this.mediumPriorityTickets = mediumPriorityTickets;
    }
    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }
    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }
    public void setUnassignedTickets(long unassignedTickets) {
        this.unassignedTickets = unassignedTickets;
    }
}
