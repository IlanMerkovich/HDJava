import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createTicket } from '../api/ticketApi'
import type { TicketPriority } from '../types/ticket'

export default function CreateTicketPage() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [priority, setPriority] = useState<TicketPriority>('MEDIUM')
    const [error, setError] = useState('')

    const createTicketMutation = useMutation({
        mutationFn: () =>
            createTicket({
                title,
                description,
                priority,
            }),
        onSuccess: async (createdTicket) => {
            setError('')
            await queryClient.invalidateQueries({ queryKey: ['tickets'] })
            navigate(`/tickets/${createdTicket.id}`)
        },
        onError: (err) => {
            if (err instanceof Error) {
                setError(err.message)
            } else {
                setError('Failed to create ticket')
            }
        },
    })

    function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()

        if (!title.trim()) {
            setError('Title is required')
            return
        }

        if (!description.trim()) {
            setError('Description is required')
            return
        }

        setError('')
        createTicketMutation.mutate()
    }

    return (
        <div className="min-h-screen bg-slate-100 p-6">
            <div className="max-w-3xl mx-auto">
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h1 className="text-3xl font-bold">Create Ticket</h1>
                        <p className="text-slate-600 mt-1">
                            Open a new help desk ticket
                        </p>
                    </div>

                    <Link
                        to="/tickets"
                        className="rounded-lg bg-slate-900 text-white px-4 py-2 font-medium"
                    >
                        Back to Tickets
                    </Link>
                </div>

                <div className="bg-white rounded-2xl shadow-lg p-6">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium mb-1">Title</label>
                            <input
                                type="text"
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                                placeholder="Enter ticket title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Description</label>
                            <textarea
                                className="w-full rounded-lg border px-3 py-2 min-h-[160px] outline-none focus:ring"
                                placeholder="Describe the issue"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Priority</label>
                            <select
                                className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
                                value={priority}
                                onChange={(e) => setPriority(e.target.value as TicketPriority)}
                            >
                                <option value="LOW">LOW</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HIGH">HIGH</option>
                            </select>
                        </div>

                        {error && (
                            <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={createTicketMutation.isPending}
                            className="rounded-lg bg-slate-900 text-white px-5 py-2.5 font-medium disabled:opacity-50"
                        >
                            {createTicketMutation.isPending ? 'Creating Ticket...' : 'Create Ticket'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    )
}