import type { BadgeTone } from '../components/ui/Badge'
import type { TicketPriority, TicketStatus } from '../types/ticket'

export function getStatusBadgeTone(status: TicketStatus): BadgeTone {
    if (status === 'OPEN') return 'blue'
    if (status === 'IN_PROGRESS') return 'amber'
    if (status === 'RESOLVED') return 'emerald'
    return 'neutral'
}

export function getPriorityBadgeTone(priority: TicketPriority): BadgeTone {
    if (priority === 'HIGH') return 'rose'
    if (priority === 'MEDIUM') return 'orange'
    return 'emerald'
}

