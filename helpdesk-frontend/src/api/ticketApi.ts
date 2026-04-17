import { apiFetch } from './client'
import type {
    PagedTicketResponse,
    TicketResponse,
    CommentResponse,
    CreateTicketRequest,
    TicketQueryParams,
    TicketStatus,
    TicketHistoryResponse,
} from '../types/ticket'

export function getTickets(params: TicketQueryParams) {
    const searchParams = new URLSearchParams()

    searchParams.set('page', String(params.page))
    searchParams.set('size', String(params.size))
    searchParams.set('sortBy', params.sortBy)
    searchParams.set('direction', params.direction)

    if (params.query && params.query.trim()) {
        searchParams.set('query', params.query.trim())
    }

    if (params.status) {
        searchParams.set('status', params.status)
    }

    if (params.priority) {
        searchParams.set('priority', params.priority)
    }

    return apiFetch<PagedTicketResponse>(`/api/tickets?${searchParams.toString()}`)
}

export function getTicketById(id: number) {
    return apiFetch<TicketResponse>(`/api/tickets/${id}`)
}

export function getTicketHistory(ticketId: number) {
    return apiFetch<TicketHistoryResponse[]>(`/api/tickets/${ticketId}/history`)
}

export function addComment(ticketId: number, content: string) {
    return apiFetch<CommentResponse>(`/api/tickets/${ticketId}/comments`, {
        method: 'POST',
        body: JSON.stringify({ content }),
    })
}

export function createTicket(data: CreateTicketRequest) {
    return apiFetch<TicketResponse>('/api/tickets', {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function updateTicketStatus(ticketId: number, status: TicketStatus) {
    return apiFetch<TicketResponse>(`/api/tickets/${ticketId}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status }),
    })
}

export function reopenTicket(ticketId: number) {
    return apiFetch<TicketResponse>(`/api/tickets/${ticketId}/reopen`, {
        method: 'PATCH',
    })
}

export function assignTicket(ticketId: number, agentId: number) {
    return apiFetch<TicketResponse>(`/api/tickets/${ticketId}/assign`, {
        method: 'PATCH',
        body: JSON.stringify({ agentId }),
    })
}

export function unassignTicket(ticketId: number) {
    return apiFetch<TicketResponse>(`/api/tickets/${ticketId}/unassign`, {
        method: 'PATCH',
    })
}