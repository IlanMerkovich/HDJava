import { useMemo, useState } from 'react'
import { Link, useParams } from 'react-router'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
    addComment,
    assignTicket,
    getTicketById,
    getTicketHistory,
    reopenTicket,
    unassignTicket,
    updateTicketStatus,
} from '../api/ticketApi'
import {
    deleteAttachment,
    downloadAttachment,
    getAttachmentsByTicketId,
    previewAttachment,
    uploadAttachment,
} from '../api/attachmentApi'
import { getUsers } from '../api/userApi'
import { Badge, Card, SectionHeader, buttonVariants } from '../components/ui'
import { getPriorityBadgeTone, getStatusBadgeTone } from '../utils/ticketBadgeTone'
import { useAuth } from '../context/AuthContext'
import type { TicketStatus } from '../types/ticket'

const surfaceCardClass = 'p-6'

export default function TicketDetailsPage() {
    const params = useParams()
    const ticketId = Number(params.id)

    const queryClient = useQueryClient()
    const { user } = useAuth()

    const [commentText, setCommentText] = useState('')
    const [commentError, setCommentError] = useState('')

    const [selectedFile, setSelectedFile] = useState<File | null>(null)
    const [attachmentError, setAttachmentError] = useState('')

    const [statusActionError, setStatusActionError] = useState('')

    const [selectedAgentId, setSelectedAgentId] = useState('')
    const [assignmentError, setAssignmentError] = useState('')

    const { data, isLoading, error } = useQuery({
        queryKey: ['ticket', ticketId],
        queryFn: () => getTicketById(ticketId),
        enabled: Number.isFinite(ticketId),
    })

    const {
        data: history,
        isLoading: historyLoading,
        error: historyError,
    } = useQuery({
        queryKey: ['ticket-history', ticketId],
        queryFn: () => getTicketHistory(ticketId),
        enabled: Number.isFinite(ticketId),
    })

    const {
        data: attachments,
        isLoading: attachmentsLoading,
        error: attachmentsError,
    } = useQuery({
        queryKey: ['attachments', ticketId],
        queryFn: () => getAttachmentsByTicketId(ticketId),
        enabled: Number.isFinite(ticketId),
    })

    const {
        data: users,
        isLoading: usersLoading,
        error: usersError,
    } = useQuery({
        queryKey: ['users'],
        queryFn: getUsers,
    })

    const agents = useMemo(
        () => (users ?? []).filter((u) => u.role === 'AGENT'),
        [users]
    )

    const isAdmin = user?.role === 'ADMIN'
    const isAgent = user?.role === 'AGENT'
    const isClient = user?.role === 'CLIENT'

    const canManageAssignment = isAdmin
    const canUploadAttachment = Boolean(user)
    const canAddComment = Boolean(user)
    const canDeleteAttachment = isAdmin
    const canReopen = isAdmin || isClient
    const canUpdateStatus = isAdmin || isAgent

    const addCommentMutation = useMutation({
        mutationFn: () => addComment(ticketId, commentText),
        onSuccess: async () => {
            setCommentText('')
            setCommentError('')
            await queryClient.invalidateQueries({ queryKey: ['ticket', ticketId] })
        },
        onError: (error) => {
            if (error instanceof Error) {
                setCommentError(error.message)
            } else {
                setCommentError('Failed to add comment')
            }
        },
    })

    const uploadAttachmentMutation = useMutation({
        mutationFn: () => {
            if (!selectedFile) {
                throw new Error('Please choose a file first')
            }
            return uploadAttachment(ticketId, selectedFile)
        },
        onSuccess: async () => {
            setSelectedFile(null)
            setAttachmentError('')
            await queryClient.invalidateQueries({ queryKey: ['attachments', ticketId] })
        },
        onError: (error) => {
            if (error instanceof Error) {
                setAttachmentError(error.message)
            } else {
                setAttachmentError('Failed to upload attachment')
            }
        },
    })

    const deleteAttachmentMutation = useMutation({
        mutationFn: (attachmentId: number) => deleteAttachment(attachmentId),
        onSuccess: async () => {
            setAttachmentError('')
            await queryClient.invalidateQueries({ queryKey: ['attachments', ticketId] })
        },
        onError: (error) => {
            if (error instanceof Error) {
                setAttachmentError(error.message)
            } else {
                setAttachmentError('Failed to delete attachment')
            }
        },
    })

    const updateStatusMutation = useMutation({
        mutationFn: (status: TicketStatus) => updateTicketStatus(ticketId, status),
        onSuccess: async () => {
            setStatusActionError('')
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['ticket', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['ticket-history', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['tickets'] }),
                queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] }),
                queryClient.invalidateQueries({ queryKey: ['notifications'] }),
            ])
        },
        onError: (error) => {
            if (error instanceof Error) {
                setStatusActionError(error.message)
            } else {
                setStatusActionError('Failed to update ticket status')
            }
        },
    })

    const reopenTicketMutation = useMutation({
        mutationFn: () => reopenTicket(ticketId),
        onSuccess: async () => {
            setStatusActionError('')
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['ticket', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['ticket-history', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['tickets'] }),
                queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] }),
                queryClient.invalidateQueries({ queryKey: ['notifications'] }),
            ])
        },
        onError: (error) => {
            if (error instanceof Error) {
                setStatusActionError(error.message)
            } else {
                setStatusActionError('Failed to reopen ticket')
            }
        },
    })

    const assignTicketMutation = useMutation({
        mutationFn: (agentId: number) => assignTicket(ticketId, agentId),
        onSuccess: async () => {
            setAssignmentError('')
            setSelectedAgentId('')
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['ticket', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['ticket-history', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['tickets'] }),
                queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] }),
                queryClient.invalidateQueries({ queryKey: ['notifications'] }),
            ])
        },
        onError: (error) => {
            if (error instanceof Error) {
                setAssignmentError(error.message)
            } else {
                setAssignmentError('Failed to assign ticket')
            }
        },
    })

    const unassignTicketMutation = useMutation({
        mutationFn: () => unassignTicket(ticketId),
        onSuccess: async () => {
            setAssignmentError('')
            await Promise.all([
                queryClient.invalidateQueries({ queryKey: ['ticket', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['ticket-history', ticketId] }),
                queryClient.invalidateQueries({ queryKey: ['tickets'] }),
                queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] }),
                queryClient.invalidateQueries({ queryKey: ['notifications'] }),
            ])
        },
        onError: (error) => {
            if (error instanceof Error) {
                setAssignmentError(error.message)
            } else {
                setAssignmentError('Failed to unassign ticket')
            }
        },
    })

    async function handleDownload(attachmentId: number, fileName: string) {
        try {
            setAttachmentError('')
            await downloadAttachment(attachmentId, fileName)
        } catch (error) {
            if (error instanceof Error) {
                setAttachmentError(error.message)
            } else {
                setAttachmentError('Failed to download attachment')
            }
        }
    }

    async function handlePreview(attachmentId: number) {
        try {
            setAttachmentError('')
            await previewAttachment(attachmentId)
        } catch (error) {
            if (error instanceof Error) {
                setAttachmentError(error.message)
            } else {
                setAttachmentError('Failed to preview attachment')
            }
        }
    }

    function renderStatusActions(status: TicketStatus) {
        const isBusy = updateStatusMutation.isPending || reopenTicketMutation.isPending

        if (status === 'OPEN' && canUpdateStatus) {
            return (
                <button
                    type="button"
                    onClick={() => updateStatusMutation.mutate('IN_PROGRESS')}
                    disabled={isBusy}
                    className={buttonVariants({ variant: 'secondary' })}
                >
                    {updateStatusMutation.isPending ? 'Updating...' : 'Set In Progress'}
                </button>
            )
        }

        if (status === 'IN_PROGRESS' && canUpdateStatus) {
            return (
                <button
                    type="button"
                    onClick={() => updateStatusMutation.mutate('RESOLVED')}
                    disabled={isBusy}
                    className={buttonVariants({ className: 'bg-amber-500 hover:bg-amber-600 text-white' })}
                >
                    {updateStatusMutation.isPending ? 'Updating...' : 'Set Resolved'}
                </button>
            )
        }

        if (status === 'RESOLVED' && canUpdateStatus) {
            return (
                <button
                    type="button"
                    onClick={() => updateStatusMutation.mutate('CLOSED')}
                    disabled={isBusy}
                    className={buttonVariants({ className: 'bg-emerald-600 hover:bg-emerald-700 text-white' })}
                >
                    {updateStatusMutation.isPending ? 'Updating...' : 'Set Closed'}
                </button>
            )
        }

        if (status === 'CLOSED' && canReopen) {
            return (
                <button
                    type="button"
                    onClick={() => reopenTicketMutation.mutate()}
                    disabled={isBusy}
                    className={buttonVariants()}
                >
                    {reopenTicketMutation.isPending ? 'Reopening...' : 'Reopen Ticket'}
                </button>
            )
        }

        return null
    }

    function formatHistoryItem(item: {
        actionType: string
        oldStatus?: string
        newStatus?: string
        oldAssignedToEmail?: string
        newAssignedToEmail?: string
    }) {
        if (item.actionType === 'STATUS_CHANGED') {
            return `${item.oldStatus ?? '-'} → ${item.newStatus ?? '-'}`
        }

        if (item.actionType === 'REOPENED') {
            return `${item.oldStatus ?? '-'} → ${item.newStatus ?? '-'}`
        }

        if (item.actionType === 'ASSIGNED') {
            return `${item.oldAssignedToEmail ?? '-'} → ${item.newAssignedToEmail ?? '-'}`
        }

        if (item.actionType === 'UNASSIGNED') {
            return `${item.oldAssignedToEmail ?? '-'} → unassigned`
        }

        return item.actionType
    }

    if (!Number.isFinite(ticketId)) {
        return (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
                    Invalid ticket id
            </div>
        )
    }

    if (isLoading) {
        return (
            <Card className="px-5 py-4 text-slate-600">
                Loading ticket details...
            </Card>
        )
    }

    if (error instanceof Error) {
        return (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
                Failed to load ticket: {error.message}
            </div>
        )
    }

    if (!data) {
        return (
            <div className="rounded-2xl border border-yellow-200 bg-yellow-50 px-4 py-3 text-yellow-700">
                Ticket not found.
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <div className="mx-auto max-w-5xl space-y-6">
                <SectionHeader
                    title={`Ticket #${data.id}`}
                    description="Ticket details, comments and attachments"
                    actions={
                        <Link
                            to="/tickets"
                            className={buttonVariants({ variant: 'neutral' })}
                        >
                            Back to Tickets
                        </Link>
                    }
                />

                <Card className={`${surfaceCardClass} space-y-6`}>
                    <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                        <div>
                            <h2 className="text-2xl font-semibold">{data.title}</h2>
                            <div className="mt-2 flex flex-wrap items-center gap-2 text-xs font-semibold">
                                <Badge tone={getStatusBadgeTone(data.status)}>
                                    {data.status}
                                </Badge>
                                <Badge tone={getPriorityBadgeTone(data.priority)}>
                                    {data.priority}
                                </Badge>
                            </div>
                            <p className="text-slate-600 mt-2 whitespace-pre-line">
                                {data.description}
                            </p>
                        </div>

                        <div className="flex flex-col items-start gap-3">
                            {renderStatusActions(data.status)}

                            {statusActionError && (
                                <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                                    {statusActionError}
                                </div>
                            )}
                        </div>
                    </div>

                    {canManageAssignment && (
                        <div className="rounded-2xl border p-5 space-y-4">
                            <div className="flex items-center justify-between gap-4">
                                <div>
                                    <h3 className="text-xl font-semibold">Assignment</h3>
                                    <p className="text-sm text-slate-500">
                                        Assign this ticket to an agent or remove current assignment
                                    </p>
                                </div>

                                {data.assignedToEmail && (
                                    <button
                                        type="button"
                                        onClick={() => unassignTicketMutation.mutate()}
                                        disabled={unassignTicketMutation.isPending || assignTicketMutation.isPending}
                                        className={buttonVariants({ variant: 'danger' })}
                                    >
                                        {unassignTicketMutation.isPending ? 'Unassigning...' : 'Unassign'}
                                    </button>
                                )}
                            </div>

                            <div className="grid gap-4 md:grid-cols-[1fr_auto] items-end">
                                <div>
                                    <label className="block text-sm font-medium mb-1">Assign to Agent</label>
                                    <select
                                        value={selectedAgentId}
                                        onChange={(e) => setSelectedAgentId(e.target.value)}
                                        className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                                        disabled={usersLoading || assignTicketMutation.isPending || unassignTicketMutation.isPending}
                                    >
                                        <option value="">Select agent</option>
                                        {agents.map((agent) => (
                                            <option key={agent.id} value={agent.id}>
                                                {agent.fullName} ({agent.email})
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <button
                                    type="button"
                                    onClick={() => {
                                        if (!selectedAgentId) {
                                            setAssignmentError('Please select an agent')
                                            return
                                        }

                                        setAssignmentError('')
                                        assignTicketMutation.mutate(Number(selectedAgentId))
                                    }}
                                    disabled={assignTicketMutation.isPending || usersLoading || unassignTicketMutation.isPending}
                                    className={buttonVariants({ variant: 'secondary' })}
                                >
                                    {assignTicketMutation.isPending ? 'Assigning...' : 'Assign'}
                                </button>
                            </div>

                            <div className="grid gap-4 md:grid-cols-2">
                                <div className="rounded-xl border p-4">
                                    <h4 className="text-sm text-slate-500 mb-1">Current Assignee</h4>
                                    <p className="font-semibold">{data.assignedToFullName ?? '-'}</p>
                                    <p className="text-sm text-slate-500">{data.assignedToEmail ?? '-'}</p>
                                </div>

                                <div className="rounded-xl border p-4">
                                    <h4 className="text-sm text-slate-500 mb-1">Agents Available</h4>
                                    {usersLoading ? (
                                        <p className="text-slate-600">Loading agents...</p>
                                    ) : usersError instanceof Error ? (
                                        <p className="text-red-600 text-sm">{usersError.message}</p>
                                    ) : (
                                        <p className="font-semibold">{agents.length}</p>
                                    )}
                                </div>
                            </div>

                            {assignmentError && (
                                <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                                    {assignmentError}
                                </div>
                            )}
                        </div>
                    )}

                    <div className="grid gap-4 md:grid-cols-2">
                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Status</h3>
                            <p>
                                <Badge tone={getStatusBadgeTone(data.status)}>
                                {data.status}
                                </Badge>
                            </p>
                        </div>

                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Priority</h3>
                            <p>
                                <Badge tone={getPriorityBadgeTone(data.priority)}>
                                {data.priority}
                                </Badge>
                            </p>
                        </div>

                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Created By</h3>
                            <p className="font-semibold">{data.createdByEmail ?? '-'}</p>
                        </div>

                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Assigned To</h3>
                            <p className="font-semibold">{data.assignedToEmail ?? '-'}</p>
                        </div>

                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Created At</h3>
                            <p className="font-semibold">
                                {new Date(data.createdAt).toLocaleString()}
                            </p>
                        </div>

                        <div className="rounded-xl border p-4">
                            <h3 className="text-sm text-slate-500 mb-1">Updated At</h3>
                            <p className="font-semibold">
                                {new Date(data.updatedAt).toLocaleString()}
                            </p>
                        </div>
                    </div>
                </Card>

                <Card className={surfaceCardClass}>
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-2xl font-bold">History</h2>
                        <span className="text-sm text-slate-600">
              {history?.length ?? 0} events
            </span>
                    </div>

                    {historyLoading ? (
                        <div className="text-slate-600">Loading history...</div>
                    ) : historyError instanceof Error ? (
                        <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                            Failed to load history: {historyError.message}
                        </div>
                    ) : history && history.length > 0 ? (
                        <div className="space-y-3">
                            {history.map((item) => (
                                <div key={item.id} className="rounded-xl border p-4">
                                    <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                                        <div>
                                            <p className="font-semibold">{item.actionType}</p>
                                            <p className="text-sm text-slate-500">
                                                By: {item.changedByFullName ?? '-'} ({item.changedByEmail ?? '-'})
                                            </p>
                                        </div>

                                        <p className="text-sm text-slate-500">
                                            {new Date(item.changedAt).toLocaleString()}
                                        </p>
                                    </div>

                                    <p className="mt-2 text-sm text-slate-700">
                                        {formatHistoryItem(item)}
                                    </p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-slate-600">No history events yet.</div>
                    )}
                </Card>

                <Card className={surfaceCardClass}>
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-2xl font-bold">Attachments</h2>
                        <span className="text-sm text-slate-600">
              {attachments?.length ?? 0} files
            </span>
                    </div>

                    {canUploadAttachment && (
                        <div className="mb-6 rounded-xl border p-4 bg-slate-50">
                            <h3 className="text-lg font-semibold mb-3">Upload Attachment</h3>

                            <form
                                onSubmit={(e) => {
                                    e.preventDefault()

                                    if (!selectedFile) {
                                        setAttachmentError('Please choose a file first')
                                        return
                                    }

                                    setAttachmentError('')
                                    uploadAttachmentMutation.mutate()
                                }}
                                className="space-y-3"
                            >
                                <input
                                    type="file"
                                    className="block w-full text-sm"
                                    onChange={(e) => {
                                        const file = e.target.files?.[0] ?? null
                                        setSelectedFile(file)
                                    }}
                                />

                                {attachmentError && (
                                    <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                                        {attachmentError}
                                    </div>
                                )}

                                <button
                                    type="submit"
                                    disabled={uploadAttachmentMutation.isPending}
                                    className={buttonVariants()}
                                >
                                    {uploadAttachmentMutation.isPending ? 'Uploading...' : 'Upload Attachment'}
                                </button>
                            </form>
                        </div>
                    )}

                    {attachmentsLoading ? (
                        <div className="text-slate-600">Loading attachments...</div>
                    ) : attachmentsError instanceof Error ? (
                        <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                            Failed to load attachments: {attachmentsError.message}
                        </div>
                    ) : attachments && attachments.length > 0 ? (
                        <div className="space-y-3">
                            {attachments.map((attachment) => (
                                <div
                                    key={attachment.id}
                                    className="rounded-xl border p-4 flex items-start justify-between gap-4"
                                >
                                    <div>
                                        <p className="font-semibold">{attachment.originalFileName}</p>
                                        <p className="text-sm text-slate-500">
                                            {attachment.contentType} · {attachment.size} bytes
                                        </p>
                                        <p className="text-sm text-slate-500">
                                            Uploaded by: {attachment.uploadedByEmail ?? '-'}
                                        </p>
                                        <p className="text-sm text-slate-500">
                                            {new Date(attachment.createdAt).toLocaleString()}
                                        </p>
                                    </div>

                                    <div className="flex flex-col items-end gap-2">
                                        <div className="text-sm text-slate-500">
                                            {attachment.attachmentFileCategory}
                                        </div>

                                        <div className="flex flex-wrap justify-end gap-2">
                                            <button
                                                type="button"
                                                onClick={() =>
                                                    handleDownload(attachment.id, attachment.originalFileName)
                                                }
                                                className={buttonVariants({ size: 'sm' })}
                                            >
                                                Download
                                            </button>

                                            {attachment.previewable && (
                                                <button
                                                    type="button"
                                                    onClick={() => handlePreview(attachment.id)}
                                                    className={buttonVariants({ variant: 'secondary', size: 'sm' })}
                                                >
                                                    Preview
                                                </button>
                                            )}

                                            {canDeleteAttachment && (
                                                <button
                                                    type="button"
                                                    onClick={() => deleteAttachmentMutation.mutate(attachment.id)}
                                                    disabled={deleteAttachmentMutation.isPending}
                                                    className={buttonVariants({ variant: 'danger', size: 'sm' })}
                                                >
                                                    {deleteAttachmentMutation.isPending ? 'Deleting...' : 'Delete'}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-slate-600">No attachments yet.</div>
                    )}
                </Card>

                <Card className={surfaceCardClass}>
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-2xl font-bold">Comments</h2>
                        <span className="text-sm text-slate-600">
              {data.commentsCount} comments
            </span>
                    </div>

                    {canAddComment && (
                        <div className="mb-6 rounded-xl border p-4 bg-slate-50">
                            <h3 className="text-lg font-semibold mb-3">Add Comment</h3>

                            <form
                                onSubmit={(e) => {
                                    e.preventDefault()

                                    if (!commentText.trim()) {
                                        setCommentError('Comment cannot be empty')
                                        return
                                    }

                                    setCommentError('')
                                    addCommentMutation.mutate()
                                }}
                                className="space-y-3"
                            >
                <textarea
                    className="w-full rounded-lg border px-3 py-2 min-h-[120px] outline-none focus:ring"
                    placeholder="Write your comment here..."
                    value={commentText}
                    onChange={(e) => setCommentText(e.target.value)}
                />

                                {commentError && (
                                    <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                                        {commentError}
                                    </div>
                                )}

                                <button
                                    type="submit"
                                    disabled={addCommentMutation.isPending}
                                    className={buttonVariants()}
                                >
                                    {addCommentMutation.isPending ? 'Adding Comment...' : 'Add Comment'}
                                </button>
                            </form>
                        </div>
                    )}

                    {data.comments.length > 0 ? (
                        <div className="space-y-4">
                            {data.comments.map((comment) => (
                                <div key={comment.id} className="rounded-xl border p-4">
                                    <div className="flex items-center justify-between gap-4 mb-2">
                                        <div>
                                            <p className="font-semibold">
                                                {comment.authorName ?? 'Unknown User'}
                                            </p>
                                            <p className="text-sm text-slate-500">
                                                {comment.authorEmail ?? '-'}
                                            </p>
                                        </div>

                                        <p className="text-sm text-slate-500">
                                            {new Date(comment.createdAt).toLocaleString()}
                                        </p>
                                    </div>

                                    <p className="text-slate-700 whitespace-pre-line">
                                        {comment.content}
                                    </p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-slate-600">No comments yet.</div>
                    )}
                </Card>
            </div>
        </div>
    )
}