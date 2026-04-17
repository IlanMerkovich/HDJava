export type AttachmentFileCategory = 'IMAGE' | 'PDF' | 'TEXT' | 'OTHER'

export interface TicketAttachmentResponse {
    id: number
    originalFileName: string
    contentType: string
    size: number
    createdAt: string
    uploadedByEmail?: string
    previewable: boolean
    attachmentFileCategory: AttachmentFileCategory
}