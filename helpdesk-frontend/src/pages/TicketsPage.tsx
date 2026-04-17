import { useState } from 'react'
import { Link } from 'react-router'
import { useQuery } from '@tanstack/react-query'
import { getTickets } from '../api/ticketApi'
import type { TicketPriority, TicketQueryParams, TicketStatus } from '../types/ticket'

const DEFAULT_PARAMS: TicketQueryParams = {
    page: 0,
    size: 10,
    sortBy: 'updatedAt',
    direction: 'desc',
    query: '',
    status: '',
    priority: '',
}

export default function TicketsPage() {
    const [searchInput, setSearchInput] = useState('')
    const [statusFilter, setStatusFilter] = useState<TicketStatus | ''>('')
    const [priorityFilter, setPriorityFilter] = useState<TicketPriority | ''>('')
    const [sortBy, setSortBy] = useState('updatedAt')
    const [direction, setDirection] = useState<'asc' | 'desc'>('desc')
    const [size, setSize] = useState(10)

    const [params, setParams] = useState<TicketQueryParams>(DEFAULT_PARAMS)

    const { data, isLoading, error } = useQuery({
        queryKey: ['tickets', params],
        queryFn: () => getTickets(params),
    })

    function handleApplyFilters() {
        setParams({
            page: 0,
            size,
            sortBy,
            direction,
            query: searchInput,
            status: statusFilter,
            priority: priorityFilter,
        })
    }

    function handleClearFilters() {
        setSearchInput('')
        setStatusFilter('')
        setPriorityFilter('')
        setSortBy('updatedAt')
        setDirection('desc')
        setSize(10)
        setParams(DEFAULT_PARAMS)
    }

    function goToPreviousPage() {
        if (!data || data.first) return

        setParams((prev) => ({
            ...prev,
            page: prev.page - 1,
        }))
    }

    function goToNextPage() {
        if (!data || data.last) return

        setParams((prev) => ({
            ...prev,
            page: prev.page + 1,
        }))
    }

    if (isLoading) {
        return <div className="p-6 text-lg">Loading tickets...</div>
    }

    if (error instanceof Error) {
        return (
            <div className="p-6">
                <div className="rounded-lg bg-red-100 text-red-700 px-4 py-3">
                    Failed to load tickets: {error.message}
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-slate-100 p-6">
            <div className="max-w-7xl mx-auto space-y-6">
                <div className="flex items-center justify-between mb-2 gap-4">
                    <div>
                        <h1 className="text-3xl font-bold">Tickets</h1>
                        <p className="text-slate-600 mt-1">
                            Search, filter and browse your tickets
                        </p>
                    </div>

                    <div className="flex items-center gap-3">
                        <Link
                            to="/tickets/new"
                            className="rounded-lg bg-blue-600 text-white px-4 py-2 font-medium"
                        >
                            Create Ticket
                        </Link>

                        <Link
                            to="/dashboard"
                            className="rounded-lg bg-slate-900 text-white px-4 py-2 font-medium"
                        >
                            Back to Dashboard
                        </Link>
                    </div>
                </div>

                <div className="bg-white rounded-2xl shadow-lg p-5">
                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        <div>
                            <label className="block text-sm font-medium mb-1">Search</label>
                            <input
                                type="text"
                                value={searchInput}
                                onChange={(e) => setSearchInput(e.target.value)}
                                placeholder="Search by title or description"
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Status</label>
                            <select
                                value={statusFilter}
                                onChange={(e) => setStatusFilter(e.target.value as TicketStatus | '')}
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            >
                                <option value="">All</option>
                                <option value="OPEN">OPEN</option>
                                <option value="IN_PROGRESS">IN_PROGRESS</option>
                                <option value="RESOLVED">RESOLVED</option>
                                <option value="CLOSED">CLOSED</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Priority</label>
                            <select
                                value={priorityFilter}
                                onChange={(e) => setPriorityFilter(e.target.value as TicketPriority | '')}
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            >
                                <option value="">All</option>
                                <option value="LOW">LOW</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HIGH">HIGH</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Sort By</label>
                            <select
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            >
                                <option value="updatedAt">updatedAt</option>
                                <option value="createdAt">createdAt</option>
                                <option value="id">id</option>
                                <option value="title">title</option>
                                <option value="status">status</option>
                                <option value="priority">priority</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Direction</label>
                            <select
                                value={direction}
                                onChange={(e) => setDirection(e.target.value as 'asc' | 'desc')}
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            >
                                <option value="desc">desc</option>
                                <option value="asc">asc</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Page Size</label>
                            <select
                                value={size}
                                onChange={(e) => setSize(Number(e.target.value))}
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                            >
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>
                        </div>
                    </div>

                    <div className="flex flex-wrap gap-3 mt-4">
                        <button
                            type="button"
                            onClick={handleApplyFilters}
                            className="rounded-lg bg-slate-900 text-white px-4 py-2 font-medium"
                        >
                            Apply Filters
                        </button>

                        <button
                            type="button"
                            onClick={handleClearFilters}
                            className="rounded-lg border px-4 py-2 font-medium"
                        >
                            Clear
                        </button>
                    </div>
                </div>

                <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
                    {data && data.content.length > 0 ? (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-slate-50 border-b">
                                <tr>
                                    <th className="px-4 py-3 text-sm font-semibold">ID</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Title</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Status</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Priority</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Created By</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Assigned To</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Comments</th>
                                    <th className="px-4 py-3 text-sm font-semibold">Updated At</th>
                                </tr>
                                </thead>

                                <tbody>
                                {data.content.map((ticket) => (
                                    <tr key={ticket.id} className="border-b last:border-b-0">
                                        <td className="px-4 py-3">{ticket.id}</td>
                                        <td className="px-4 py-3 font-medium">
                                            <Link
                                                to={`/tickets/${ticket.id}`}
                                                className="text-blue-600 hover:underline"
                                            >
                                                {ticket.title}
                                            </Link>
                                        </td>
                                        <td className="px-4 py-3">{ticket.status}</td>
                                        <td className="px-4 py-3">{ticket.priority}</td>
                                        <td className="px-4 py-3">{ticket.createdByEmail ?? '-'}</td>
                                        <td className="px-4 py-3">{ticket.assignedToEmail ?? '-'}</td>
                                        <td className="px-4 py-3">{ticket.commentsCount}</td>
                                        <td className="px-4 py-3">
                                            {new Date(ticket.updatedAt).toLocaleString()}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="p-6 text-slate-600">No tickets found.</div>
                    )}
                </div>

                {data && (
                    <div className="bg-white rounded-2xl shadow-lg p-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                        <div className="text-sm text-slate-600">
                            Page {data.page + 1} of {Math.max(data.totalPages, 1)} | Total tickets:{' '}
                            {data.totalElements}
                        </div>

                        <div className="flex items-center gap-3">
                            <button
                                type="button"
                                onClick={goToPreviousPage}
                                disabled={data.first}
                                className="rounded-lg border px-4 py-2 font-medium disabled:opacity-50"
                            >
                                Previous
                            </button>

                            <button
                                type="button"
                                onClick={goToNextPage}
                                disabled={data.last}
                                className="rounded-lg border px-4 py-2 font-medium disabled:opacity-50"
                            >
                                Next
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}