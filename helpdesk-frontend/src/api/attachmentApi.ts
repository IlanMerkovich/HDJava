import { API_BASE_URL, apiFetch } from './client'
import type { TicketAttachmentResponse } from '../types/attachment'

export function getAttachmentsByTicketId(ticketId: number) {
    return apiFetch<TicketAttachmentResponse[]>(`/api/tickets/${ticketId}/attachments`)
}

export async function uploadAttachment(ticketId: number, file: File): Promise<TicketAttachmentResponse> {
    const token = localStorage.getItem('token')

    const formData = new FormData()
    formData.append('file', file)

    const headers = new Headers()
    if (token) {
        headers.set('Authorization', `Bearer ${token}`)
    }

    const response = await fetch(`${API_BASE_URL}/api/tickets/${ticketId}/attachments`, {
        method: 'POST',
        headers,
        body: formData,
    })

    const contentType = response.headers.get('content-type') || ''

    let responseData: unknown
    if (contentType.includes('application/json')) {
        responseData = await response.json()
    } else {
        responseData = await response.text()
    }

    if (!response.ok) {
        if (
            responseData &&
            typeof responseData === 'object' &&
            'message' in responseData &&
            typeof responseData.message === 'string'
        ) {
            throw new Error(responseData.message)
        }

        if (typeof responseData === 'string' && responseData.trim() !== '') {
            throw new Error(responseData)
        }

        throw new Error(`Request failed with status ${response.status}`)
    }

    return responseData as TicketAttachmentResponse
}

async function fetchAttachmentBlob(path: string): Promise<Blob> {
    const token = localStorage.getItem('token')

    const headers = new Headers()
    if (token) {
        headers.set('Authorization', `Bearer ${token}`)
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method: 'GET',
        headers,
    })

    if (!response.ok) {
        const contentType = response.headers.get('content-type') || ''

        let responseData: unknown
        if (contentType.includes('application/json')) {
            responseData = await response.json()
        } else {
            responseData = await response.text()
        }

        if (
            responseData &&
            typeof responseData === 'object' &&
            'message' in responseData &&
            typeof responseData.message === 'string'
        ) {
            throw new Error(responseData.message)
        }

        if (typeof responseData === 'string' && responseData.trim() !== '') {
            throw new Error(responseData)
        }

        throw new Error(`Request failed with status ${response.status}`)
    }

    return response.blob()
}

export async function downloadAttachment(attachmentId: number, fileName: string) {
    const blob = await fetchAttachmentBlob(`/api/attachments/${attachmentId}/download`)
    const url = window.URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    link.remove()

    window.URL.revokeObjectURL(url)
}

export async function previewAttachment(attachmentId: number) {
    const previewWindow = window.open('', '_blank')

    const blob = await fetchAttachmentBlob(`/api/attachments/${attachmentId}/preview`)
    const url = window.URL.createObjectURL(blob)

    if (previewWindow) {
        previewWindow.location.href = url
    } else {
        window.open(url, '_blank')
    }

    setTimeout(() => {
        window.URL.revokeObjectURL(url)
    }, 60_000)
}

export function deleteAttachment(attachmentId: number) {
    return apiFetch<void>(`/api/attachments/${attachmentId}`, {
        method: 'DELETE',
    })
}