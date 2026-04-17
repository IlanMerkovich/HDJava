import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { useQuery } from '@tanstack/react-query'
import { getTickets } from '../api/ticketApi'
import { Badge, Card, SectionHeader, Skeleton } from '../components/ui'
import { buttonVariants, formControlVariants } from '../components/ui'
import { getPriorityBadgeTone, getStatusBadgeTone } from '../utils/ticketBadgeTone'
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
    const SEARCH_DEBOUNCE_MS = 450

    const [searchInput, setSearchInput] = useState('')
    const [statusFilter, setStatusFilter] = useState<TicketStatus | ''>('')
    const [priorityFilter, setPriorityFilter] = useState<TicketPriority | ''>('')
    const [sortBy, setSortBy] = useState('updatedAt')
    const [direction, setDirection] = useState<'asc' | 'desc'>('desc')
    const [size, setSize] = useState(10)

    const [params, setParams] = useState<TicketQueryParams>(DEFAULT_PARAMS)

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            const normalizedQuery = searchInput.trim()

            setParams((prev) => {
                if (prev.query === normalizedQuery) {
                    return prev
                }

                return {
                    ...prev,
                    page: 0,
                    query: normalizedQuery,
                }
            })
        }, SEARCH_DEBOUNCE_MS)

        return () => {
            window.clearTimeout(timeoutId)
        }
    }, [searchInput])

    const { data, isLoading, isFetching, error } = useQuery({
        queryKey: ['tickets', params],
        queryFn: () => getTickets(params),
    })

    function handleApplyFilters() {
        setParams({
            page: 0,
            size,
            sortBy,
            direction,
            query: searchInput.trim(),
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
        return (
            <div className="space-y-6">
                <Card className="p-5 sm:p-6">
                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        {Array.from({ length: 6 }).map((_, index) => (
                            <div key={`filter-skeleton-${index}`} className="space-y-2">
                                <Skeleton className="h-3 w-20" />
                                <Skeleton className="h-10 w-full" />
                            </div>
                        ))}
                    </div>
                    <div className="mt-5 flex gap-3">
                        <Skeleton className="h-10 w-28" />
                        <Skeleton className="h-10 w-20" />
                    </div>
                </Card>

                <Card className="overflow-hidden p-4">
                    <div className="space-y-3">
                        {Array.from({ length: 8 }).map((_, index) => (
                            <div key={`row-skeleton-${index}`} className="grid grid-cols-4 gap-3 md:grid-cols-8">
                                {Array.from({ length: 8 }).map((__, cellIndex) => (
                                    <Skeleton key={`cell-${index}-${cellIndex}`} className="h-5 w-full" />
                                ))}
                            </div>
                        ))}
                    </div>
                </Card>
            </div>
        )
    }

    if (error instanceof Error) {
        return (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
                Failed to load tickets: {error.message}
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <div className="space-y-6">
                <SectionHeader
                    title="Tickets"
                    description="Search, filter, and sort tickets quickly with a clean queue-style view."
                    actions={
                        <>
                            <Link
                                to="/tickets/new"
                                className={buttonVariants({ variant: 'secondary' })}
                            >
                                Create Ticket
                            </Link>

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
                    <div className="mb-4 flex items-center justify-between gap-3">
                        <h2 className="text-lg font-semibold text-slate-900">Filters</h2>
                        <div className="text-right">
                            <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Refine results</p>
                            {isFetching && !isLoading && (
                                <p className="mt-1 inline-flex items-center gap-1.5 rounded-full border border-slate-200 bg-white px-2 py-0.5 text-xs font-medium text-slate-600">
                                    <span className="relative flex h-2 w-2">
                                        <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-blue-400 opacity-75" />
                                        <span className="relative inline-flex h-2 w-2 rounded-full bg-blue-500" />
                                    </span>
                                    Searching...
                                </p>
                            )}
                        </div>
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Search</label>
                            <input
                                type="text"
                                value={searchInput}
                                onChange={(e) => setSearchInput(e.target.value)}
                                placeholder="Search by title or description"
                                className={formControlVariants()}
                            />
                        </div>

                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Status</label>
                            <select
                                value={statusFilter}
                                onChange={(e) => setStatusFilter(e.target.value as TicketStatus | '')}
                                className={formControlVariants()}
                            >
                                <option value="">All</option>
                                <option value="OPEN">OPEN</option>
                                <option value="IN_PROGRESS">IN_PROGRESS</option>
                                <option value="RESOLVED">RESOLVED</option>
                                <option value="CLOSED">CLOSED</option>
                            </select>
                        </div>

                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Priority</label>
                            <select
                                value={priorityFilter}
                                onChange={(e) => setPriorityFilter(e.target.value as TicketPriority | '')}
                                className={formControlVariants()}
                            >
                                <option value="">All</option>
                                <option value="LOW">LOW</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HIGH">HIGH</option>
                            </select>
                        </div>

                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Sort By</label>
                            <select
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                                className={formControlVariants()}
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
                            <label className="mb-1 block text-sm font-medium text-slate-700">Direction</label>
                            <select
                                value={direction}
                                onChange={(e) => setDirection(e.target.value as 'asc' | 'desc')}
                                className={formControlVariants()}
                            >
                                <option value="desc">desc</option>
                                <option value="asc">asc</option>
                            </select>
                        </div>

                        <div>
                            <label className="mb-1 block text-sm font-medium text-slate-700">Page Size</label>
                            <select
                                value={size}
                                onChange={(e) => setSize(Number(e.target.value))}
                                className={formControlVariants()}
                            >
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>
                        </div>
                    </div>

                    <div className="mt-5 flex flex-wrap items-center gap-3">
                        <button
                            type="button"
                            onClick={handleApplyFilters}
                            className={buttonVariants()}
                        >
                            Apply Filters
                        </button>

                        <button
                            type="button"
                            onClick={handleClearFilters}
                            className={buttonVariants({ variant: 'neutral' })}
                        >
                            Clear
                        </button>
                    </div>
                </Card>

                <Card className="overflow-hidden">
                    {data && data.content.length > 0 ? (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-sm">
                                <thead className="border-b border-slate-200 bg-slate-50">
                                <tr>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">ID</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Title</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Status</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Priority</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Created By</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Assigned To</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Comments</th>
                                    <th className="px-4 py-3 text-xs font-semibold uppercase tracking-wide text-slate-600">Updated At</th>
                                </tr>
                                </thead>

                                <tbody>
                                {data.content.map((ticket) => (
                                    <tr key={ticket.id} className="border-b border-slate-100 transition-colors hover:bg-slate-50 last:border-b-0">
                                        <td className="px-4 py-3 font-mono text-xs text-slate-500">#{ticket.id}</td>
                                        <td className="px-4 py-3 font-medium text-slate-900">
                                            <Link
                                                to={`/tickets/${ticket.id}`}
                                                className="block max-w-[320px] truncate text-blue-700 hover:text-blue-800 hover:underline"
                                                title={ticket.title}
                                            >
                                                {ticket.title}
                                            </Link>
                                        </td>
                                        <td className="px-4 py-3">
                                            <Badge tone={getStatusBadgeTone(ticket.status)} className="uppercase tracking-wide">
                                                {ticket.status}
                                            </Badge>
                                        </td>
                                        <td className="px-4 py-3">
                                            <Badge tone={getPriorityBadgeTone(ticket.priority)} className="uppercase tracking-wide">
                                                {ticket.priority}
                                            </Badge>
                                        </td>
                                        <td className="px-4 py-3">
                                            <span className="block max-w-[220px] truncate" title={ticket.createdByEmail ?? '-'}>
                                                {ticket.createdByEmail ?? '-'}
                                            </span>
                                        </td>
                                        <td className="px-4 py-3">
                                            <span className="block max-w-[220px] truncate" title={ticket.assignedToEmail ?? '-'}>
                                                {ticket.assignedToEmail ?? '-'}
                                            </span>
                                        </td>
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
                        <div className="p-6">
                            <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-4 py-3 text-slate-600">
                                No tickets found.
                            </div>
                        </div>
                    )}
                </Card>

                {data && (
                    <Card className="flex flex-col gap-3 p-4 md:flex-row md:items-center md:justify-between">
                        <div className="text-sm text-slate-600">
                            Page {data.page + 1} of {Math.max(data.totalPages, 1)} | Total tickets:{' '}
                            {data.totalElements}
                        </div>

                        <div className="flex items-center gap-3">
                            <button
                                type="button"
                                onClick={goToPreviousPage}
                                disabled={data.first}
                                className={buttonVariants({ variant: 'neutral' })}
                            >
                                Previous
                            </button>

                            <button
                                type="button"
                                onClick={goToNextPage}
                                disabled={data.last}
                                className={buttonVariants({ variant: 'neutral' })}
                            >
                                Next
                            </button>
                        </div>
                    </Card>
                )}
            </div>
        </div>
    )
}