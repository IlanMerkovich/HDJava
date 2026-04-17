import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createTicket } from '../api/ticketApi'
import { Card, SectionHeader, buttonVariants, formControlVariants, useToast } from '../components/ui'
import type { TicketPriority } from '../types/ticket'

export default function CreateTicketPage() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const toast = useToast()

    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [priority, setPriority] = useState<TicketPriority>('MEDIUM')
    const [error, setError] = useState('')

    const TITLE_MIN_LENGTH = 3
    const TITLE_MAX_LENGTH = 100
    const DESCRIPTION_MIN_LENGTH = 5
    const DESCRIPTION_MAX_LENGTH = 500

    const createTicketMutation = useMutation({
        mutationFn: () =>
            createTicket({
                title: title.trim(),
                description: description.trim(),
                priority,
            }),
        onSuccess: async (createdTicket) => {
            setError('')
            await queryClient.invalidateQueries({ queryKey: ['tickets'] })
            toast.success('Ticket created successfully')
            navigate(`/tickets/${createdTicket.id}`)
        },
        onError: (err) => {
            if (err instanceof Error) {
                setError(err.message)
                toast.error(err.message)
            } else {
                setError('Failed to create ticket')
                toast.error('Failed to create ticket')
            }
        },
    })

    function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()

        const normalizedTitle = title.trim()
        const normalizedDescription = description.trim()

        if (!normalizedTitle) {
            setError('Title is required')
            return
        }

        if (normalizedTitle.length < TITLE_MIN_LENGTH || normalizedTitle.length > TITLE_MAX_LENGTH) {
            setError(`Title must be between ${TITLE_MIN_LENGTH} and ${TITLE_MAX_LENGTH} characters`)
            return
        }

        if (!normalizedDescription) {
            setError('Description is required')
            return
        }

        if (
            normalizedDescription.length < DESCRIPTION_MIN_LENGTH ||
            normalizedDescription.length > DESCRIPTION_MAX_LENGTH
        ) {
            setError(`Description must be between ${DESCRIPTION_MIN_LENGTH} and ${DESCRIPTION_MAX_LENGTH} characters`)
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
                                className={formControlVariants()}
                                placeholder="Enter ticket title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                minLength={TITLE_MIN_LENGTH}
                                maxLength={TITLE_MAX_LENGTH}
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Description</label>
                            <textarea
                                className={formControlVariants({ size: 'lg', className: 'min-h-[160px]' })}
                                placeholder="Describe the issue"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                minLength={DESCRIPTION_MIN_LENGTH}
                                maxLength={DESCRIPTION_MAX_LENGTH}
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-1">Priority</label>
                            <select
                                className={formControlVariants()}
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