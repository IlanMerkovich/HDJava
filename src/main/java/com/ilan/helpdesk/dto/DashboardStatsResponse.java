package com.ilan.helpdesk.dto;

public class DashboardStatsResponse {
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    public DashboardStatsResponse(){}

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }
    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }
    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }
    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }
    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }
    public long getClosedTickets() {
        return closedTickets;
    }
    public long getTotalTickets() {
        return totalTickets;
    }
    public long getResolvedTickets() {
        return resolvedTickets;
    }
    public long getOpenTickets() {
        return openTickets;
    }
    public long getInProgressTickets() {
        return inProgressTickets;
    }

}
