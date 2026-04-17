export interface NotificationResponse {
    id: number
    message: string
    read: boolean
    createdAt: string
    ticketId?: number | null
    notificationType?: string
    type?: string
}