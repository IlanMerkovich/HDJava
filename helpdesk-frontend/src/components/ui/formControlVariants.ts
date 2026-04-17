type FormControlSize = 'sm' | 'md' | 'lg'

type FormControlOptions = {
    size?: FormControlSize
    className?: string
}

const sizeClassMap: Record<FormControlSize, string> = {
    sm: 'h-9 px-3 text-sm',
    md: 'h-10 px-3 text-sm',
    lg: 'px-3 py-3 text-base',
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function formControlVariants(options: FormControlOptions = {}) {
    const { size = 'md', className } = options

    return joinClasses(
        'w-full rounded-xl border border-slate-300 bg-white text-slate-900 shadow-sm outline-none transition-colors placeholder:text-slate-400 focus:border-slate-500 focus:ring-2 focus:ring-slate-200 disabled:cursor-not-allowed disabled:bg-slate-100',
        sizeClassMap[size],
        className
    )
}

export type { FormControlSize, FormControlOptions }

