import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router'
import { useAuth } from '../context/AuthContext'
import { getDashboardSummary } from '../api/dashboardApi'
import { Badge, Card, SectionHeader } from '../components/ui'
import { getPriorityBadgeTone, getStatusBadgeTone } from '../utils/ticketBadgeTone'

const cardClass = 'p-5'
const cardHeaderClass = 'mb-1 text-xs font-semibold uppercase tracking-wide text-slate-500'

export default function DashboardPage() {
    const { user } = useAuth()

    const { data, isLoading, error } = useQuery({
        queryKey: ['dashboard-summary'],
        queryFn: getDashboardSummary,
    })

    return (
        <div className="space-y-6">
            <Card className="p-6">
                <SectionHeader
                    title="Dashboard"
                    description={`Welcome back${user ? `, ${user.fullName}` : ''}`}
                />
            </Card>

            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
                <Card className={cardClass}>
                    <h2 className={cardHeaderClass}>Full Name</h2>
                    <p className="text-lg font-semibold text-slate-900">{user?.fullName ?? '-'}</p>
                </Card>

                <Card className={cardClass}>
                    <h2 className={cardHeaderClass}>Email</h2>
                    <p className="text-lg font-semibold text-slate-900">{user?.email ?? '-'}</p>
                </Card>

                <Card className={cardClass}>
                    <h2 className={cardHeaderClass}>Role</h2>
                    <p className="text-lg font-semibold text-slate-900">{user?.role ?? '-'}</p>
                </Card>

                <Card className={cardClass}>
                    <h2 className={cardHeaderClass}>Status</h2>
                    <p>
                        <Badge tone="emerald" className="px-3 py-1 text-sm">
                        Authenticated
                        </Badge>
                    </p>
                </Card>
            </div>

            {isLoading ? (
                <Card className="p-6 text-lg text-slate-600">
                    Loading dashboard summary...
                </Card>
            ) : error instanceof Error ? (
                <Card className="p-6">
                    <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                        Failed to load dashboard: {error.message}
                    </div>
                </Card>
            ) : data ? (
                <>
                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Total Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.totalTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Open</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.openTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>In Progress</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.inProgressTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Resolved</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.resolvedTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Closed</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.closedTickets}</p>
                        </Card>
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Assigned Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.assignmentDashboard.assignedTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>Unassigned Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.assignmentDashboard.unassignedTickets}</p>
                        </Card>

                        <Card className={cardClass}>
                            <h2 className={cardHeaderClass}>High Priority Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.priorityResponse.highPriorityTickets}</p>
                        </Card>
                    </div>

                    <div className="grid gap-6 xl:grid-cols-2">
                        <Card className="p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold tracking-tight text-slate-900">Recent Tickets</h2>
                                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                                    {data.recentTickets.length} items
                                </span>
                            </div>

                            {data.recentTickets.length > 0 ? (
                                <div className="space-y-3">
                                    {data.recentTickets.map((ticket) => (
                                        <Link
                                            key={ticket.id}
                                            to={`/tickets/${ticket.id}`}
                                            className="block rounded-xl border border-slate-200 p-4 transition-colors hover:bg-slate-50"
                                        >
                                            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                                                <div>
                                                    <p className="font-semibold text-slate-900">{ticket.title}</p>
                                                    <div className="mt-1 flex flex-wrap gap-2 text-xs font-semibold">
                                                        <Badge tone={getStatusBadgeTone(ticket.status)}>
                                                            {ticket.status}
                                                        </Badge>
                                                        <Badge tone={getPriorityBadgeTone(ticket.priority)}>
                                                            {ticket.priority}
                                                        </Badge>
                                                    </div>
                                                </div>

                                                <div className="text-sm text-slate-500">
                                                    {new Date(ticket.createdAt).toLocaleString()}
                                                </div>
                                            </div>
                                        </Link>
                                    ))}
                                </div>
                            ) : (
                                <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-4 text-slate-600">
                                    No recent tickets.
                                </div>
                            )}
                        </Card>

                        <Card className="p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold tracking-tight text-slate-900">Recently Updated</h2>
                                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                                    {data.recentlyUpdatedTickets.length} items
                                </span>
                            </div>

                            {data.recentlyUpdatedTickets.length > 0 ? (
                                <div className="space-y-3">
                                    {data.recentlyUpdatedTickets.map((ticket) => (
                                        <Link
                                            key={ticket.id}
                                            to={`/tickets/${ticket.id}`}
                                            className="block rounded-xl border border-slate-200 p-4 transition-colors hover:bg-slate-50"
                                        >
                                            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                                                <div>
                                                    <p className="font-semibold text-slate-900">{ticket.title}</p>
                                                    <div className="mt-1 flex flex-wrap gap-2 text-xs font-semibold">
                                                        <Badge tone={getStatusBadgeTone(ticket.status)}>
                                                            {ticket.status}
                                                        </Badge>
                                                        <Badge tone={getPriorityBadgeTone(ticket.priority)}>
                                                            {ticket.priority}
                                                        </Badge>
                                                    </div>
                                                </div>

                                                <div className="text-sm text-slate-500">
                                                    {new Date(ticket.updatedAt).toLocaleString()}
                                                </div>
                                            </div>
                                        </Link>
                                    ))}
                                </div>
                            ) : (
                                <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-4 text-slate-600">
                                    No recently updated tickets.
                                </div>
                            )}
                        </Card>
                    </div>

                    {data.agentWorkload.length > 0 && (
                        <Card className="p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold tracking-tight text-slate-900">Agent Workload</h2>
                                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                                    {data.agentWorkload.length} agents
                                </span>
                            </div>

                            <div className="overflow-x-auto">
                                <table className="w-full text-left text-sm">
                                    <thead className="border-b bg-slate-50">
                                    <tr>
                                        <th className="px-4 py-3 text-sm font-semibold">Agent</th>
                                        <th className="px-4 py-3 text-sm font-semibold">Email</th>
                                        <th className="px-4 py-3 text-sm font-semibold">Assigned</th>
                                        <th className="px-4 py-3 text-sm font-semibold">Open</th>
                                        <th className="px-4 py-3 text-sm font-semibold">In Progress</th>
                                        <th className="px-4 py-3 text-sm font-semibold">Resolved</th>
                                        <th className="px-4 py-3 text-sm font-semibold">Closed</th>
                                        <th className="px-4 py-3 text-sm font-semibold">High Priority</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {data.agentWorkload.map((agent) => (
                                        <tr key={agent.agentId} className="border-b border-slate-100 last:border-b-0">
                                            <td className="px-4 py-3 font-medium text-slate-900">{agent.agentName}</td>
                                            <td className="px-4 py-3">{agent.agentEmail}</td>
                                            <td className="px-4 py-3">{agent.assignedTickets}</td>
                                            <td className="px-4 py-3">{agent.openedTickets}</td>
                                            <td className="px-4 py-3">{agent.inProgressTickets}</td>
                                            <td className="px-4 py-3">{agent.resolvedTickets}</td>
                                            <td className="px-4 py-3">{agent.closedTickets}</td>
                                            <td className="px-4 py-3">{agent.highPriorityTickets}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        </Card>
                    )}
                </>
            ) : null}
        </div>
    )
}