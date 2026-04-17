export type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH'

export interface CommentResponse {
    id: number
    content: string
    authorEmail?: string
    authorName?: string
    createdAt: string
}

export interface TicketResponse {
    id: number
    title: string
    description: string
    status: TicketStatus
    priority: TicketPriority
    createdByEmail?: string
    assignedToEmail?: string
    assignedToFullName?: string
    createdAt: string
    updatedAt: string
    commentsCount: number
    comments: CommentResponse[]
}

export interface PagedTicketResponse {
    content: TicketResponse[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
}

export interface CreateTicketRequest {
    title: string
    description: string
    priority: TicketPriority
}

export interface TicketQueryParams {
    page: number
    size: number
    sortBy: string
    direction: 'asc' | 'desc'
    query?: string
    status?: TicketStatus | ''
    priority?: TicketPriority | ''
}

export interface TicketHistoryResponse {
    id: number
    actionType: string
    oldStatus?: TicketStatus
    newStatus?: TicketStatus
    oldAssignedToEmail?: string
    newAssignedToEmail?: string
    changedAt: string
    changedByEmail?: string
    changedByFullName?: string
}