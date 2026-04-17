type ButtonVariant = 'primary' | 'secondary' | 'neutral' | 'danger'
type ButtonSize = 'sm' | 'md' | 'lg'

type ButtonVariantOptions = {
    variant?: ButtonVariant
    size?: ButtonSize
    className?: string
}

const baseClass =
    'inline-flex items-center justify-center rounded-xl font-semibold transition-all duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-300 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50'

const variantClassMap: Record<ButtonVariant, string> = {
    primary: 'bg-slate-900 text-white shadow-sm hover:bg-slate-800',
    secondary: 'bg-blue-600 text-white shadow-sm hover:bg-blue-700',
    neutral: 'border border-slate-300 bg-white text-slate-700 hover:border-slate-400 hover:bg-slate-50',
    danger: 'bg-red-600 text-white shadow-sm hover:bg-red-700',
}

const sizeClassMap: Record<ButtonSize, string> = {
    sm: 'h-8 px-3 text-xs',
    md: 'h-10 px-4 text-sm',
    lg: 'h-11 px-5 text-sm',
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function buttonVariants(options: ButtonVariantOptions = {}) {
    const { variant = 'primary', size = 'md', className } = options

    return joinClasses(baseClass, variantClassMap[variant], sizeClassMap[size], className)
}

export type { ButtonVariant, ButtonSize, ButtonVariantOptions }

