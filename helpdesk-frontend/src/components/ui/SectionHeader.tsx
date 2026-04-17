import type { ReactNode } from 'react'

type SectionHeaderProps = {
    title: string
    description?: string
    actions?: ReactNode
    className?: string
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function SectionHeader({ title, description, actions, className }: SectionHeaderProps) {
    return (
        <div className={joinClasses('flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between', className)}>
            <div>
                <h1 className="text-3xl font-bold tracking-tight text-slate-900">{title}</h1>
                {description && <p className="mt-1 text-slate-600">{description}</p>}
            </div>

            {actions && <div className="flex flex-wrap items-center gap-3">{actions}</div>}
        </div>
    )
}

