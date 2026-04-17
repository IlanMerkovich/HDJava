import type { TicketResponse } from './ticket'

export interface DashboardStatsResponse {
    openTickets: number
    closedTickets: number
    inProgressTickets: number
    resolvedTickets: number
    totalTickets: number
}

export interface DashboardAssignmentResponse {
    assignedTickets: number
    unassignedTickets: number
}

export interface DashboardPriorityResponse {
    highPriorityTickets: number
    mediumPriorityTickets: number
    lowPriorityTickets: number
}

export interface AgentWorkloadResponse {
    agentId: number
    agentName: string
    agentEmail: string
    openedTickets: number
    closedTickets: number
    inProgressTickets: number
    resolvedTickets: number
    assignedTickets: number
    highPriorityTickets: number
}

export interface DashboardSummaryResponse {
    statsResponse: DashboardStatsResponse
    assignmentDashboard: DashboardAssignmentResponse
    priorityResponse: DashboardPriorityResponse
    recentTickets: TicketResponse[]
    recentlyUpdatedTickets: TicketResponse[]
    agentWorkload: AgentWorkloadResponse[]
}