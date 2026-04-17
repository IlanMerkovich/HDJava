import type { HTMLAttributes, ReactNode } from 'react'

type CardProps = HTMLAttributes<HTMLDivElement> & {
    children: ReactNode
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function Card({ children, className, ...props }: CardProps) {
    return (
        <div
            className={joinClasses(
                'rounded-2xl border border-slate-200/90 bg-white shadow-[0_1px_2px_rgba(15,23,42,0.06),0_12px_28px_rgba(15,23,42,0.04)] ring-1 ring-white/70 transition-shadow duration-200',
                className
            )}
            {...props}
        >
            {children}
        </div>
    )
}

