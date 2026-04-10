package com.ilan.helpdesk.dto;

public class DashboardPriorityResponse {
    private long lowPriorityTickets;
    private long mediumPriorityTickets;
    private long highPriorityTickets;
    public DashboardPriorityResponse(){}

    public long getMediumPriorityTickets() {
        return mediumPriorityTickets;
    }
    public long getLowPriorityTickets() {
        return lowPriorityTickets;
    }
    public long getHighPriorityTickets() {
        return highPriorityTickets;
    }
    public void setMediumPriorityTickets(long mediumPriorityTickets) {
        this.mediumPriorityTickets = mediumPriorityTickets;
    }
    public void setLowPriorityTickets(long lowPriorityTickets) {
        this.lowPriorityTickets = lowPriorityTickets;
    }
    public void setHighPriorityTickets(long highPriorityTickets) {
        this.highPriorityTickets = highPriorityTickets;
    }
}
