import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router'
import { useAuth } from '../context/AuthContext'
import { getDashboardSummary } from '../api/dashboardApi'
import { Badge, Card, SectionHeader, buttonVariants } from '../components/ui'
import { getPriorityBadgeTone, getStatusBadgeTone } from '../utils/ticketBadgeTone'

const statCardClass = 'p-5 transition-all duration-200 hover:-translate-y-0.5 hover:shadow-[0_10px_24px_rgba(15,23,42,0.08)]'
const statLabelClass = 'text-xs font-semibold uppercase tracking-wide text-slate-500'

function getInitials(fullName?: string) {
    if (!fullName) {
        return 'HD'
    }

    const parts = fullName.trim().split(/\s+/).filter(Boolean)
    if (parts.length === 0) {
        return 'HD'
    }

    const initials = parts.slice(0, 2).map((part) => part[0]?.toUpperCase() ?? '')
    return initials.join('') || 'HD'
}

function getTimeGreeting() {
    const hour = new Date().getHours()

    if (hour < 12) {
        return 'Good morning'
    }

    if (hour < 18) {
        return 'Good afternoon'
    }

    return 'Good evening'
}

export default function DashboardPage() {
    const { user } = useAuth()

    const { data, isLoading, error } = useQuery({
        queryKey: ['dashboard-summary'],
        queryFn: getDashboardSummary,
    })

    return (
        <div className="space-y-6">
            <SectionHeader
                title="Dashboard"
                description="Track team throughput, recent changes, and your daily workload in one place."
                actions={
                    <Link to="/tickets/new" className={buttonVariants({ variant: 'secondary' })}>
                        Create Ticket
                    </Link>
                }
            />

            <Card className="overflow-hidden border-slate-200 bg-gradient-to-r from-slate-900 via-slate-800 to-slate-700 p-0 text-white shadow-md">
                <div className="flex flex-col gap-6 p-6 md:flex-row md:items-center md:justify-between md:p-7">
                    <div className="flex items-start gap-4">
                        <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl border border-white/10 bg-white/10 text-lg font-bold text-white shadow-inner backdrop-blur">
                            {getInitials(user?.fullName)}
                        </div>

                        <div className="space-y-3">
                            <div className="inline-flex rounded-full bg-white/10 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-slate-100">
                                Help Desk overview
                            </div>

                            <div>
                                <h1 className="text-3xl font-bold tracking-tight md:text-4xl">
                                    {getTimeGreeting()}, {user?.fullName ?? 'user'}.
                                </h1>
                                <p className="mt-2 max-w-2xl text-sm text-slate-200 md:text-base">
                                    Here is your latest ticket activity, workload, and quick account snapshot.
                                </p>
                            </div>
                        </div>
                    </div>

                    <div className="grid w-full gap-3 sm:grid-cols-2 md:max-w-[320px] md:flex-1">
                        <div className="rounded-2xl border border-white/10 bg-white/10 p-4 backdrop-blur">
                            <p className="text-xs uppercase tracking-wide text-slate-300">Name</p>
                            <p className="mt-1 text-sm font-semibold text-white">{user?.fullName ?? '-'}</p>
                        </div>

                        <div className="rounded-2xl border border-white/10 bg-white/10 p-4 backdrop-blur">
                            <p className="text-xs uppercase tracking-wide text-slate-300">Role</p>
                            <p className="mt-1 text-sm font-semibold text-white">{user?.role ?? '-'}</p>
                        </div>
                    </div>
                </div>
            </Card>

            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
                <Card className={statCardClass}>
                    <h2 className={statLabelClass}>Full Name</h2>
                    <p className="text-lg font-semibold text-slate-900">{user?.fullName ?? '-'}</p>
                </Card>

                <Card className={`${statCardClass} min-w-0`}>
                    <h2 className={statLabelClass}>Email</h2>
                    <p className="truncate text-sm font-semibold leading-6 text-slate-900 md:text-base" title={user?.email ?? '-'}>
                        {user?.email ?? '-'}
                    </p>
                </Card>

                <Card className={statCardClass}>
                    <h2 className={statLabelClass}>Role</h2>
                    <p className="text-lg font-semibold text-slate-900">{user?.role ?? '-'}</p>
                </Card>

                <Card className={statCardClass}>
                    <h2 className={statLabelClass}>Status</h2>
                    <Badge tone="emerald" className="px-3 py-1 text-sm">
                        Authenticated
                    </Badge>
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
                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Total Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.totalTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Open</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.openTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>In Progress</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.inProgressTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Resolved</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.resolvedTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Closed</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.statsResponse.closedTickets}</p>
                        </Card>
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Assigned Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.assignmentDashboard.assignedTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>Unassigned Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.assignmentDashboard.unassignedTickets}</p>
                        </Card>

                        <Card className={statCardClass}>
                            <h2 className={statLabelClass}>High Priority Tickets</h2>
                            <p className="text-3xl font-bold text-slate-900">{data.priorityResponse.highPriorityTickets}</p>
                        </Card>
                    </div>

                    <div className="grid gap-6 xl:grid-cols-2">
                        <Card className="p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-xl font-bold tracking-tight text-slate-900">Recent Tickets</h2>
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
                                            className="block rounded-xl border border-slate-200 p-4 transition-colors hover:border-slate-300 hover:bg-slate-50"
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
                                <h2 className="text-xl font-bold tracking-tight text-slate-900">Recently Updated</h2>
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
                                            className="block rounded-xl border border-slate-200 p-4 transition-colors hover:border-slate-300 hover:bg-slate-50"
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

                            <div className="overflow-x-auto rounded-xl border border-slate-200">
                                <table className="w-full text-left text-sm">
                                    <thead className="border-b bg-slate-50">
                                    <tr>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Agent</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Email</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Assigned</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Open</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">In Progress</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Resolved</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Closed</th>
                                        <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">High Priority</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {data.agentWorkload.map((agent) => (
                                        <tr key={agent.agentId} className="border-b border-slate-100 transition-colors hover:bg-slate-50 last:border-b-0">
                                            <td className="px-4 py-3 font-medium text-slate-900">{agent.agentName}</td>
                                            <td className="px-4 py-3">
                                                <span className="block max-w-[240px] truncate" title={agent.agentEmail}>
                                                    {agent.agentEmail}
                                                </span>
                                            </td>
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