import { useMemo, useRef, useState } from 'react'
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
import { Badge, Card, ConfirmDialog, SectionHeader, buttonVariants, formControlVariants } from '../components/ui'
import { getPriorityBadgeTone, getStatusBadgeTone } from '../utils/ticketBadgeTone'
import { useAuth } from '../context/AuthContext'
import type { TicketAttachmentResponse } from '../types/attachment'
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
    const fileInputRef = useRef<HTMLInputElement | null>(null)
    const [attachmentError, setAttachmentError] = useState('')
    const [attachmentToDelete, setAttachmentToDelete] = useState<TicketAttachmentResponse | null>(null)

    const [statusActionError, setStatusActionError] = useState('')
    const [isUnassignConfirmOpen, setIsUnassignConfirmOpen] = useState(false)

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
        mutationFn: (file: File) => uploadAttachment(ticketId, file),
        onSuccess: async () => {
            setSelectedFile(null)
            if (fileInputRef.current) {
                fileInputRef.current.value = ''
            }
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
            setAttachmentToDelete(null)
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
            <SectionHeader
                title={`Ticket #${data.id}`}
                description="Review status, assignment, timeline, comments, and attachments."
                actions={
                    <Link
                        to="/tickets"
                        className={buttonVariants({ variant: 'neutral' })}
                    >
                        Back to Tickets
                    </Link>
                }
            />

            <div className="grid gap-6 xl:grid-cols-12">
                <div className="space-y-6 xl:col-span-8">
                    <Card className={`${surfaceCardClass} space-y-4`}>
                        <div className="space-y-3">
                            <h2 className="text-2xl font-semibold tracking-tight text-slate-900">{data.title}</h2>
                            <div className="flex flex-wrap items-center gap-2">
                                <Badge tone={getStatusBadgeTone(data.status)} className="uppercase tracking-wide">
                                    {data.status}
                                </Badge>
                                <Badge tone={getPriorityBadgeTone(data.priority)} className="uppercase tracking-wide">
                                    {data.priority}
                                </Badge>
                            </div>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm leading-6 text-slate-700 whitespace-pre-line">
                            {data.description}
                        </div>
                    </Card>

                    <Card className={surfaceCardClass}>
                        <div className="mb-4 flex items-center justify-between">
                            <h3 className="text-xl font-bold tracking-tight text-slate-900">History</h3>
                            <span className="text-sm text-slate-600">{history?.length ?? 0} events</span>
                        </div>

                        {historyLoading ? (
                            <div className="text-slate-600">Loading history...</div>
                        ) : historyError instanceof Error ? (
                            <div className="rounded-lg bg-red-100 px-4 py-3 text-red-700">
                                Failed to load history: {historyError.message}
                            </div>
                        ) : history && history.length > 0 ? (
                            <div className="space-y-4">
                                {history.map((item) => (
                                    <div key={item.id} className="relative pl-6">
                                        <span className="absolute left-0 top-2 h-2.5 w-2.5 rounded-full bg-slate-400" />
                                        <span className="absolute left-[4px] top-5 h-[calc(100%-4px)] w-px bg-slate-200" />
                                        <div className="rounded-xl border border-slate-200 bg-white p-4">
                                            <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                                                <div>
                                                    <p className="font-semibold text-slate-900">{item.actionType}</p>
                                                    <p className="text-sm text-slate-500">
                                                        By: {item.changedByFullName ?? '-'} ({item.changedByEmail ?? '-'})
                                                    </p>
                                                </div>
                                                <p className="text-sm text-slate-500">{new Date(item.changedAt).toLocaleString()}</p>
                                            </div>
                                            <p className="mt-2 text-sm text-slate-700">{formatHistoryItem(item)}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-4 py-3 text-slate-600">
                                No history events yet.
                            </div>
                        )}
                    </Card>

                    <Card className={surfaceCardClass}>
                        <div className="mb-4 flex items-center justify-between">
                            <h3 className="text-xl font-bold tracking-tight text-slate-900">Comments</h3>
                            <span className="text-sm text-slate-600">{data.commentsCount} comments</span>
                        </div>

                        {canAddComment && (
                            <div className="mb-6 rounded-xl border border-slate-200 bg-slate-50 p-4">
                                <h4 className="mb-3 text-base font-semibold text-slate-900">Add Comment</h4>
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
                                        className={formControlVariants({ size: 'lg', className: 'min-h-[120px]' })}
                                        placeholder="Write your comment here..."
                                        value={commentText}
                                        onChange={(e) => setCommentText(e.target.value)}
                                    />

                                    {commentError && (
                                        <div className="rounded-lg bg-red-100 px-3 py-2 text-sm text-red-700">
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
                                    <div key={comment.id} className="rounded-xl border border-slate-200 bg-white p-4">
                                        <div className="mb-2 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                                            <div>
                                                <p className="font-semibold text-slate-900">{comment.authorName ?? 'Unknown User'}</p>
                                                <p className="text-sm text-slate-500">{comment.authorEmail ?? '-'}</p>
                                            </div>
                                            <p className="text-sm text-slate-500">{new Date(comment.createdAt).toLocaleString()}</p>
                                        </div>
                                        <p className="whitespace-pre-line text-slate-700">{comment.content}</p>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-4 py-3 text-slate-600">
                                No comments yet.
                            </div>
                        )}
                    </Card>
                </div>

                <div className="space-y-6 xl:col-span-4">
                    <Card className={surfaceCardClass}>
                        <h3 className="mb-4 text-lg font-bold tracking-tight text-slate-900">Ticket Details</h3>
                        <div className="space-y-3 text-sm">
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Status</p>
                                <div className="mt-1">
                                    <Badge tone={getStatusBadgeTone(data.status)}>{data.status}</Badge>
                                </div>
                            </div>
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Priority</p>
                                <div className="mt-1">
                                    <Badge tone={getPriorityBadgeTone(data.priority)}>{data.priority}</Badge>
                                </div>
                            </div>
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Created By</p>
                                <p className="mt-1 font-medium text-slate-800">{data.createdByEmail ?? '-'}</p>
                            </div>
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Assigned To</p>
                                <p className="mt-1 font-medium text-slate-800">{data.assignedToEmail ?? '-'}</p>
                            </div>
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Created At</p>
                                <p className="mt-1 font-medium text-slate-800">{new Date(data.createdAt).toLocaleString()}</p>
                            </div>
                            <div className="rounded-xl border border-slate-200 p-3">
                                <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Updated At</p>
                                <p className="mt-1 font-medium text-slate-800">{new Date(data.updatedAt).toLocaleString()}</p>
                            </div>
                        </div>
                    </Card>

                    <Card className={surfaceCardClass}>
                        <h3 className="mb-3 text-lg font-bold tracking-tight text-slate-900">Status Actions</h3>
                        <div className="space-y-3">
                            {renderStatusActions(data.status) ?? (
                                <p className="text-sm text-slate-600">No status actions available for your role.</p>
                            )}
                            {statusActionError && (
                                <div className="rounded-lg bg-red-100 px-3 py-2 text-sm text-red-700">
                                    {statusActionError}
                                </div>
                            )}
                        </div>
                    </Card>

                    {canManageAssignment && (
                        <Card className={surfaceCardClass}>
                            <div className="mb-4 flex items-center justify-between gap-2">
                                <h3 className="text-lg font-bold tracking-tight text-slate-900">Assignment</h3>
                                {data.assignedToEmail && (
                                    <button
                                        type="button"
                                        onClick={() => setIsUnassignConfirmOpen(true)}
                                        disabled={unassignTicketMutation.isPending || assignTicketMutation.isPending}
                                        className={buttonVariants({ variant: 'danger', size: 'sm' })}
                                    >
                                        {unassignTicketMutation.isPending ? 'Unassigning...' : 'Unassign'}
                                    </button>
                                )}
                            </div>

                            <div className="space-y-3">
                                <div>
                                    <label className="mb-1 block text-sm font-medium text-slate-700">Assign to Agent</label>
                                    <select
                                        value={selectedAgentId}
                                        onChange={(e) => setSelectedAgentId(e.target.value)}
                                        className={formControlVariants()}
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
                                    className={buttonVariants({ variant: 'secondary', className: 'w-full' })}
                                >
                                    {assignTicketMutation.isPending ? 'Assigning...' : 'Assign'}
                                </button>

                                <div className="rounded-xl border border-slate-200 p-3">
                                    <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Current Assignee</p>
                                    <p className="mt-1 font-medium text-slate-800">{data.assignedToFullName ?? '-'}</p>
                                    <p className="text-sm text-slate-500">{data.assignedToEmail ?? '-'}</p>
                                </div>

                                <div className="rounded-xl border border-slate-200 p-3">
                                    <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Agents Available</p>
                                    {usersLoading ? (
                                        <p className="mt-1 text-sm text-slate-600">Loading agents...</p>
                                    ) : usersError instanceof Error ? (
                                        <p className="mt-1 text-sm text-red-600">{usersError.message}</p>
                                    ) : (
                                        <p className="mt-1 font-medium text-slate-800">{agents.length}</p>
                                    )}
                                </div>

                                {assignmentError && (
                                    <div className="rounded-lg bg-red-100 px-3 py-2 text-sm text-red-700">
                                        {assignmentError}
                                    </div>
                                )}
                            </div>
                        </Card>
                    )}

                    <Card className={surfaceCardClass}>
                        <div className="mb-4 flex items-center justify-between">
                            <h3 className="text-lg font-bold tracking-tight text-slate-900">Attachments</h3>
                            <span className="text-sm text-slate-600">{attachments?.length ?? 0} files</span>
                        </div>

                        {canUploadAttachment && (
                            <form
                                onSubmit={(e) => {
                                    e.preventDefault()

                                    const file = fileInputRef.current?.files?.[0] ?? selectedFile

                                    if (!file) {
                                        setAttachmentError('Please choose a file first')
                                        return
                                    }

                                    setAttachmentError('')
                                    uploadAttachmentMutation.mutate(file)
                                }}
                                className="mb-4 space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-3"
                            >
                                <input
                                    ref={fileInputRef}
                                    type="file"
                                    className="hidden"
                                    onChange={(e) => {
                                        const file = e.target.files?.[0] ?? null
                                        setSelectedFile(file)
                                        if (file) {
                                            setAttachmentError('')
                                        }
                                    }}
                                />

                                <div className="flex flex-wrap items-center gap-2">
                                    <button
                                        type="button"
                                        onClick={() => fileInputRef.current?.click()}
                                        className={buttonVariants({ variant: 'neutral', size: 'sm' })}
                                    >
                                        Choose File
                                    </button>
                                    {selectedFile ? (
                                        <span className="max-w-[220px] truncate text-xs text-slate-600" title={selectedFile.name}>
                                            {selectedFile.name}
                                        </span>
                                    ) : (
                                        <span className="text-xs text-slate-500">No file selected</span>
                                    )}
                                </div>

                                <button
                                    type="submit"
                                    disabled={uploadAttachmentMutation.isPending || !selectedFile}
                                    className={buttonVariants({ className: 'w-full' })}
                                >
                                    {uploadAttachmentMutation.isPending ? 'Uploading...' : 'Upload Attachment'}
                                </button>
                            </form>
                        )}

                        {attachmentError && (
                            <div className="mb-4 rounded-lg bg-red-100 px-3 py-2 text-sm text-red-700">
                                {attachmentError}
                            </div>
                        )}

                        {attachmentsLoading ? (
                            <div className="text-slate-600">Loading attachments...</div>
                        ) : attachmentsError instanceof Error ? (
                            <div className="rounded-lg bg-red-100 px-4 py-3 text-red-700">
                                Failed to load attachments: {attachmentsError.message}
                            </div>
                        ) : attachments && attachments.length > 0 ? (
                            <div className="space-y-3">
                                {attachments.map((attachment) => (
                                    <div key={attachment.id} className="rounded-xl border border-slate-200 p-3">
                                        <p className="font-medium text-slate-900">{attachment.originalFileName}</p>
                                        <p className="text-xs text-slate-500">{attachment.contentType} · {attachment.size} bytes</p>
                                        <p className="text-xs text-slate-500">{new Date(attachment.createdAt).toLocaleString()}</p>
                                        <div className="mt-3 flex flex-wrap gap-2">
                                            <button
                                                type="button"
                                                onClick={() => handleDownload(attachment.id, attachment.originalFileName)}
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
                                                    onClick={() => setAttachmentToDelete(attachment)}
                                                    disabled={deleteAttachmentMutation.isPending}
                                                    className={buttonVariants({ variant: 'danger', size: 'sm' })}
                                                >
                                                    {deleteAttachmentMutation.isPending ? 'Deleting...' : 'Delete'}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-4 py-3 text-slate-600">
                                No attachments yet.
                            </div>
                        )}
                    </Card>
                </div>
            </div>

            <ConfirmDialog
                open={isUnassignConfirmOpen}
                title="Unassign ticket?"
                message="This will remove the current agent assignment from the ticket."
                confirmLabel="Unassign"
                isPending={unassignTicketMutation.isPending}
                onCancel={() => setIsUnassignConfirmOpen(false)}
                onConfirm={() => {
                    setIsUnassignConfirmOpen(false)
                    unassignTicketMutation.mutate()
                }}
            />

            <ConfirmDialog
                open={Boolean(attachmentToDelete)}
                title="Delete attachment?"
                message={`This will permanently delete ${attachmentToDelete?.originalFileName ?? 'this attachment'}.`}
                confirmLabel="Delete attachment"
                isPending={deleteAttachmentMutation.isPending}
                onCancel={() => setAttachmentToDelete(null)}
                onConfirm={() => {
                    if (!attachmentToDelete) {
                        return
                    }

                    const attachmentId = attachmentToDelete.id
                    setAttachmentToDelete(null)
                    deleteAttachmentMutation.mutate(attachmentId)
                }}
            />
        </div>
    )
}