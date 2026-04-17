import type { HTMLAttributes } from 'react'

type SkeletonProps = HTMLAttributes<HTMLDivElement>

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function Skeleton({ className, ...props }: SkeletonProps) {
    return (
        <div
            className={joinClasses('animate-pulse rounded-lg bg-slate-200/80', className)}
            {...props}
        />
    )
}

