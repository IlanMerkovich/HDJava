import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createTicket } from '../api/ticketApi'
import { Card, SectionHeader } from '../components/ui'
import { buttonVariants } from '../components/ui'
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
        <div className="space-y-6">
            <div className="mx-auto max-w-3xl">
                <SectionHeader
                    className="mb-6"
                    title="Create Ticket"
                    description="Open a new help desk ticket"
                    actions={
                        <Link
                            to="/tickets"
                            className={buttonVariants({ variant: 'neutral' })}
                        >
                            Back to Tickets
                        </Link>
                    }
                />

                <Card className="p-6">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium mb-1">Title</label>
                            <input
                                type="text"
                                className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                                placeholder="Enter ticket title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Description</label>
                            <textarea
                                className="w-full min-h-[160px] rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                                placeholder="Describe the issue"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Priority</label>
                            <select
                                className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
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
                            className={buttonVariants({ size: 'lg' })}
                        >
                            {createTicketMutation.isPending ? 'Creating Ticket...' : 'Create Ticket'}
                        </button>
                    </form>
                </Card>
            </div>
        </div>
    )
}