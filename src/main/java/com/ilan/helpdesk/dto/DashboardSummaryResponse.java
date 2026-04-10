package com.ilan.helpdesk.dto;

import com.ilan.helpdesk.model.Ticket;

import java.util.List;

public class DashboardSummaryResponse {
    private DashboardAssignmentResonse assignmentDashboard;
    private DashboardStatsResponse statsResponse;
    private DashboardPriorityResponse priorityResponse;
    private List<TicketResponse> recentTickets;
    private List<TicketResponse> recentlyUpdatedTickets;
    private List<AgentWorkloadResponse> agentWorkload;

    public DashboardSummaryResponse(){}

    public List<AgentWorkloadResponse> getAgentWorkload() {
        return agentWorkload;
    }

    public void setAgentWorkload(List<AgentWorkloadResponse> agentWorkload) {
        this.agentWorkload = agentWorkload;
    }

    public DashboardAssignmentResonse getAssignmentDashboard() {
        return assignmentDashboard;
    }

    public DashboardPriorityResponse getPriorityResponse() {
        return priorityResponse;
    }

    public DashboardStatsResponse getStatsResponse() {
        return statsResponse;
    }

    public List<TicketResponse> getRecentlyUpdatedTickets() {
        return recentlyUpdatedTickets;
    }

    public List<TicketResponse> getRecentTickets() {
        return recentTickets;
    }

    public void setAssignmentDashboard(DashboardAssignmentResonse assignmentDashboard) {
        this.assignmentDashboard = assignmentDashboard;
    }

    public void setPriorityResponse(DashboardPriorityResponse priorityResponse) {
        this.priorityResponse = priorityResponse;
    }

    public void setRecentlyUpdatedTickets(List<TicketResponse> recentlyUpdatedTickets) {
        this.recentlyUpdatedTickets = recentlyUpdatedTickets;
    }

    public void setRecentTickets(List<TicketResponse> recentTickets) {
        this.recentTickets = recentTickets;
    }

    public void setStatsResponse(DashboardStatsResponse statsResponse) {
        this.statsResponse = statsResponse;
    }
}
