import { useState } from 'react'
import { Link } from 'react-router'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
    getNotifications,
    markAllNotificationsAsRead,
    markNotificationAsRead,
} from '../api/notificationApi'
import { Badge, Card, ConfirmDialog, SectionHeader, buttonVariants } from '../components/ui'

export default function NotificationsPage() {
    const queryClient = useQueryClient()
    const [isMarkAllConfirmOpen, setIsMarkAllConfirmOpen] = useState(false)

    const { data, isLoading, error } = useQuery({
        queryKey: ['notifications'],
        queryFn: getNotifications,
    })

    const markOneAsReadMutation = useMutation({
        mutationFn: (id: number) => markNotificationAsRead(id),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['notifications'] })
        },
    })

    const markAllAsReadMutation = useMutation({
        mutationFn: markAllNotificationsAsRead,
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['notifications'] })
        },
    })

    if (isLoading) {
        return (
            <Card className="px-5 py-4 text-slate-600">
                Loading notifications...
            </Card>
        )
    }

    if (error instanceof Error) {
        return (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
                Failed to load notifications: {error.message}
            </div>
        )
    }

    const notifications = data ?? []
    const unreadCount = notifications.filter((n) => !n.read).length

    return (
        <div className="space-y-6">
            <div className="mx-auto max-w-5xl space-y-6">
                <SectionHeader
                    title="Notifications"
                    description="Review updates from ticket activity and manage unread items quickly."
                    actions={
                        <>
                            <button
                                type="button"
                                onClick={() => setIsMarkAllConfirmOpen(true)}
                                disabled={markAllAsReadMutation.isPending || unreadCount === 0}
                                className={buttonVariants()}
                            >
                                {markAllAsReadMutation.isPending ? 'Marking...' : 'Mark all as read'}
                            </button>

                            <Link
                                to="/dashboard"
                                className={buttonVariants({ variant: 'neutral' })}
                            >
                                Back to Dashboard
                            </Link>
                        </>
                    }
                />

                <Card className="p-5 sm:p-6">
                    <div className="grid gap-4 sm:grid-cols-3">
                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <h2 className="mb-1 text-xs font-semibold uppercase tracking-wide text-slate-500">Total</h2>
                            <p className="text-2xl font-bold text-slate-900">{notifications.length}</p>
                        </div>

                        <div className="rounded-xl border border-blue-200 bg-blue-50 p-4">
                            <h2 className="mb-1 text-xs font-semibold uppercase tracking-wide text-blue-700">Unread</h2>
                            <p className="text-2xl font-bold text-slate-900">{unreadCount}</p>
                        </div>

                        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
                            <h2 className="mb-1 text-xs font-semibold uppercase tracking-wide text-slate-500">Read</h2>
                            <p className="text-2xl font-bold text-slate-900">{notifications.length - unreadCount}</p>
                        </div>
                    </div>
                </Card>

                <Card className="p-6">
                    {notifications.length > 0 ? (
                        <div className="space-y-4">
                            {notifications.map((notification) => (
                                <div
                                    key={notification.id}
                                    className={`rounded-xl border p-4 ${
                                        notification.read
                                            ? 'border-slate-200 bg-white'
                                            : 'border-blue-200 bg-blue-50/70 shadow-sm'
                                    }`}
                                >
                                    <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                                        <div className="space-y-2">
                                            <div className="flex flex-wrap items-center gap-2">
                                                <Badge tone={notification.read ? 'neutral' : 'blue'} className={notification.read ? '' : 'bg-blue-600 text-white ring-blue-600'}>
                                                    {notification.read ? 'Read' : 'Unread'}
                                                </Badge>

                                                {(notification.notificationType ?? notification.type) && (
                                                    <Badge>
                                                        {notification.notificationType ?? notification.type}
                                                    </Badge>
                                                )}
                                            </div>

                                            <p className="text-base font-medium text-slate-900">
                                                {notification.message}
                                            </p>

                                            <p className="text-sm text-slate-500">
                                                {new Date(notification.createdAt).toLocaleString()}
                                            </p>
                                        </div>

                                        <div className="flex flex-wrap gap-2">
                                            {notification.ticketId && (
                                                <Link
                                                    to={`/tickets/${notification.ticketId}`}
                                                    className={buttonVariants({ variant: 'secondary', size: 'sm' })}
                                                >
                                                    Open Ticket
                                                </Link>
                                            )}

                                            {!notification.read && (
                                                <button
                                                    type="button"
                                                    onClick={() => markOneAsReadMutation.mutate(notification.id)}
                                                    disabled={markOneAsReadMutation.isPending}
                                                    className={buttonVariants({ size: 'sm' })}
                                                >
                                                    {markOneAsReadMutation.isPending ? 'Updating...' : 'Mark as Read'}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-4 py-3 text-slate-600">
                            No notifications yet.
                        </div>
                    )}
                </Card>

                <ConfirmDialog
                    open={isMarkAllConfirmOpen}
                    title="Mark all as read?"
                    message={`This will mark ${unreadCount} notification${unreadCount === 1 ? '' : 's'} as read.`}
                    confirmLabel="Mark as read"
                    isPending={markAllAsReadMutation.isPending}
                    onCancel={() => setIsMarkAllConfirmOpen(false)}
                    onConfirm={() => {
                        setIsMarkAllConfirmOpen(false)
                        markAllAsReadMutation.mutate()
                    }}
                />
            </div>
        </div>
    )
}