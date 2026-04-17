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
        <div className={joinClasses('flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between', className)}>
            <div className="space-y-2">
                <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">{title}</h1>
                {description && (
                    <p className="max-w-2xl text-sm leading-6 text-slate-600 sm:text-base">
                        {description}
                    </p>
                )}
            </div>

            {actions && <div className="flex flex-wrap items-center gap-2.5 lg:justify-end">{actions}</div>}
        </div>
    )
}

