package com.ilan.helpdesk.dto;

public interface DashboardCountsProjection {

    long getTotalTickets();

    long getOpenTickets();
    long getInProgressTickets();
    long getResolvedTickets();
    long getClosedTickets();

    long getHighPriorityTickets();
    long getMediumPriorityTickets();
    long getLowPriorityTickets();

    long getAssignedTickets();
    long getUnassignedTickets();
}