import { apiFetch } from './client'
import type { NotificationResponse } from '../types/notification'

export function getNotifications() {
    return apiFetch<NotificationResponse[]>('/api/notifications')
}

export function markNotificationAsRead(id: number) {
    return apiFetch<NotificationResponse>(`/api/notifications/${id}/read`, {
        method: 'PATCH',
    })
}

export function markAllNotificationsAsRead() {
    return apiFetch<void>('/api/notifications/read-all', {
        method: 'PATCH',
    })
}