import { useQuery } from '@tanstack/react-query'
import { useAuth } from '../context/AuthContext'
import { getDashboardSummary } from '../api/dashboardApi'

export default function DashboardPage() {
    const { user } = useAuth()

    const { data, isLoading, error } = useQuery({
        queryKey: ['dashboard-summary'],
        queryFn: getDashboardSummary,
    })

    return (
        <div className="max-w-7xl mx-auto space-y-6">
            <div className="bg-white rounded-2xl shadow-lg p-6">
                <div>
                    <h1 className="text-3xl font-bold">Dashboard</h1>
                    <p className="text-slate-600 mt-1">
                        Welcome back{user ? `, ${user.fullName}` : ''}
                    </p>
                </div>
            </div>

            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
                <div className="rounded-2xl bg-white shadow-lg p-5">
                    <h2 className="text-sm text-slate-500 mb-1">Full Name</h2>
                    <p className="text-lg font-semibold">{user?.fullName ?? '-'}</p>
                </div>

                <div className="rounded-2xl bg-white shadow-lg p-5">
                    <h2 className="text-sm text-slate-500 mb-1">Email</h2>
                    <p className="text-lg font-semibold">{user?.email ?? '-'}</p>
                </div>

                <div className="rounded-2xl bg-white shadow-lg p-5">
                    <h2 className="text-sm text-slate-500 mb-1">Role</h2>
                    <p className="text-lg font-semibold">{user?.role ?? '-'}</p>
                </div>

                <div className="rounded-2xl bg-white shadow-lg p-5">
                    <h2 className="text-sm text-slate-500 mb-1">Status</h2>
                    <p className="text-lg font-semibold text-green-600">Authenticated</p>
                </div>
            </div>

            {isLoading ? (
                <div className="rounded-2xl bg-white shadow-lg p-6 text-lg">
                    Loading dashboard...
                </div>
            ) : error instanceof Error ? (
                <div className="rounded-2xl bg-white shadow-lg p-6">
                    <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                        Failed to load dashboard: {error.message}
                    </div>
                </div>
            ) : data ? (
                <>
                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Total Tickets</h2>
                            <p className="text-3xl font-bold">{data.statsResponse.totalTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Open</h2>
                            <p className="text-3xl font-bold">{data.statsResponse.openTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">In Progress</h2>
                            <p className="text-3xl font-bold">{data.statsResponse.inProgressTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Resolved</h2>
                            <p className="text-3xl font-bold">{data.statsResponse.resolvedTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Closed</h2>
                            <p className="text-3xl font-bold">{data.statsResponse.closedTickets}</p>
                        </div>
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Assigned Tickets</h2>
                            <p className="text-3xl font-bold">{data.assignmentDashboard.assignedTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">Unassigned Tickets</h2>
                            <p className="text-3xl font-bold">{data.assignmentDashboard.unassignedTickets}</p>
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-5">
                            <h2 className="text-sm text-slate-500 mb-1">High Priority Tickets</h2>
                            <p className="text-3xl font-bold">{data.priorityResponse.highPriorityTickets}</p>
                        </div>
                    </div>

                    <div className="grid gap-6 xl:grid-cols-2">
                        <div className="rounded-2xl bg-white shadow-lg p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold">Recent Tickets</h2>
                                <span className="text-sm text-slate-500">
                  {data.recentTickets.length} items
                </span>
                            </div>

                            {data.recentTickets.length > 0 ? (
                                <div className="space-y-3">
                                    {data.recentTickets.map((ticket) => (
                                        <a
                                            key={ticket.id}
                                            href={`/tickets/${ticket.id}`}
                                            className="block rounded-xl border p-4 hover:bg-slate-50"
                                        >
                                            <div className="flex items-center justify-between gap-4">
                                                <div>
                                                    <p className="font-semibold">{ticket.title}</p>
                                                    <p className="text-sm text-slate-500">
                                                        {ticket.status} · {ticket.priority}
                                                    </p>
                                                </div>

                                                <div className="text-sm text-slate-500">
                                                    {new Date(ticket.createdAt).toLocaleString()}
                                                </div>
                                            </div>
                                        </a>
                                    ))}
                                </div>
                            ) : (
                                <div className="text-slate-600">No recent tickets.</div>
                            )}
                        </div>

                        <div className="rounded-2xl bg-white shadow-lg p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold">Recently Updated</h2>
                                <span className="text-sm text-slate-500">
                  {data.recentlyUpdatedTickets.length} items
                </span>
                            </div>

                            {data.recentlyUpdatedTickets.length > 0 ? (
                                <div className="space-y-3">
                                    {data.recentlyUpdatedTickets.map((ticket) => (
                                        <a
                                            key={ticket.id}
                                            href={`/tickets/${ticket.id}`}
                                            className="block rounded-xl border p-4 hover:bg-slate-50"
                                        >
                                            <div className="flex items-center justify-between gap-4">
                                                <div>
                                                    <p className="font-semibold">{ticket.title}</p>
                                                    <p className="text-sm text-slate-500">
                                                        {ticket.status} · {ticket.priority}
                                                    </p>
                                                </div>

                                                <div className="text-sm text-slate-500">
                                                    {new Date(ticket.updatedAt).toLocaleString()}
                                                </div>
                                            </div>
                                        </a>
                                    ))}
                                </div>
                            ) : (
                                <div className="text-slate-600">No recently updated tickets.</div>
                            )}
                        </div>
                    </div>

                    {data.agentWorkload.length > 0 && (
                        <div className="rounded-2xl bg-white shadow-lg p-6">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-2xl font-bold">Agent Workload</h2>
                                <span className="text-sm text-slate-500">
                  {data.agentWorkload.length} agents
                </span>
                            </div>

                            <div className="overflow-x-auto">
                                <table className="w-full text-left">
                                    <thead className="bg-slate-50 border-b">
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
                                        <tr key={agent.agentId} className="border-b last:border-b-0">
                                            <td className="px-4 py-3 font-medium">{agent.agentName}</td>
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
                        </div>
                    )}
                </>
            ) : null}
        </div>
    )
}