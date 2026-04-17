import { Link } from 'react-router'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
    getNotifications,
    markAllNotificationsAsRead,
    markNotificationAsRead,
} from '../api/notificationApi'

export default function NotificationsPage() {
    const queryClient = useQueryClient()

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
        return <div className="p-6 text-lg">Loading notifications...</div>
    }

    if (error instanceof Error) {
        return (
            <div className="p-6">
                <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                    Failed to load notifications: {error.message}
                </div>
            </div>
        )
    }

    const notifications = data ?? []
    const unreadCount = notifications.filter((n) => !n.read).length

    return (
        <div className="min-h-screen bg-slate-100 p-6">
            <div className="max-w-5xl mx-auto space-y-6">
                <div className="flex items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl font-bold">Notifications</h1>
                        <p className="text-slate-600 mt-1">
                            View and manage your notifications
                        </p>
                    </div>

                    <div className="flex items-center gap-3">
                        <button
                            type="button"
                            onClick={() => markAllAsReadMutation.mutate()}
                            disabled={markAllAsReadMutation.isPending || unreadCount === 0}
                            className="rounded-lg bg-slate-900 text-white px-4 py-2 font-medium disabled:opacity-50"
                        >
                            {markAllAsReadMutation.isPending ? 'Marking...' : 'Mark all as read'}
                        </button>

                        <Link
                            to="/dashboard"
                            className="rounded-lg border px-4 py-2 font-medium"
                        >
                            Back to Dashboard
                        </Link>
                    </div>
                </div>

                <div className="bg-white rounded-2xl shadow-lg p-5">
                    <div className="flex flex-wrap items-center gap-4">
                        <div className="rounded-xl border p-4 min-w-[180px]">
                            <h2 className="text-sm text-slate-500 mb-1">Total</h2>
                            <p className="text-2xl font-bold">{notifications.length}</p>
                        </div>

                        <div className="rounded-xl border p-4 min-w-[180px]">
                            <h2 className="text-sm text-slate-500 mb-1">Unread</h2>
                            <p className="text-2xl font-bold">{unreadCount}</p>
                        </div>

                        <div className="rounded-xl border p-4 min-w-[180px]">
                            <h2 className="text-sm text-slate-500 mb-1">Read</h2>
                            <p className="text-2xl font-bold">{notifications.length - unreadCount}</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-2xl shadow-lg p-6">
                    {notifications.length > 0 ? (
                        <div className="space-y-4">
                            {notifications.map((notification) => (
                                <div
                                    key={notification.id}
                                    className={`rounded-xl border p-4 ${
                                        notification.read ? 'bg-white' : 'bg-blue-50'
                                    }`}
                                >
                                    <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
                                        <div className="space-y-2">
                                            <div className="flex flex-wrap items-center gap-2">
                        <span
                            className={`inline-block rounded-full px-2.5 py-1 text-xs font-medium ${
                                notification.read
                                    ? 'bg-slate-200 text-slate-700'
                                    : 'bg-blue-600 text-white'
                            }`}
                        >
                          {notification.read ? 'Read' : 'Unread'}
                        </span>

                                                {(notification.notificationType ?? notification.type) && (
                                                    <span className="inline-block rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-700">
                            {notification.notificationType ?? notification.type}
                          </span>
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
                                                    className="rounded-lg bg-blue-600 text-white px-3 py-1.5 text-sm font-medium"
                                                >
                                                    Open Ticket
                                                </Link>
                                            )}

                                            {!notification.read && (
                                                <button
                                                    type="button"
                                                    onClick={() => markOneAsReadMutation.mutate(notification.id)}
                                                    disabled={markOneAsReadMutation.isPending}
                                                    className="rounded-lg bg-slate-900 text-white px-3 py-1.5 text-sm font-medium disabled:opacity-50"
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
                        <div className="text-slate-600">No notifications yet.</div>
                    )}
                </div>
            </div>
        </div>
    )
}