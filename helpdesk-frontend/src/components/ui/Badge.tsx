import type { ReactNode } from 'react'

export type BadgeTone =
    | 'neutral'
    | 'blue'
    | 'amber'
    | 'emerald'
    | 'rose'
    | 'orange'

const toneClassMap: Record<BadgeTone, string> = {
    neutral: 'bg-slate-100 text-slate-700',
    blue: 'bg-blue-100 text-blue-700',
    amber: 'bg-amber-100 text-amber-700',
    emerald: 'bg-emerald-100 text-emerald-700',
    rose: 'bg-rose-100 text-rose-700',
    orange: 'bg-orange-100 text-orange-700',
}

type BadgeProps = {
    children: ReactNode
    tone?: BadgeTone
    className?: string
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function Badge({ children, tone = 'neutral', className }: BadgeProps) {
    return (
        <span
            className={joinClasses(
                'inline-flex rounded-full px-2.5 py-1 text-xs font-semibold',
                toneClassMap[tone],
                className
            )}
        >
            {children}
        </span>
    )
}

