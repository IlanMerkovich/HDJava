package com.ilan.helpdesk.dto;

public interface DashboardAgentWorkloadProjection {
    long getAgentId();
    String getAgentEmail();
    String getAgentFullName();
    long getAssignedTickets();
    long getOpenTickets();
    long getInProgressTickets();
    long getResolvedTickets();
    long getClosedTickets();
    long getHighPriorityTickets();
}
