type ButtonVariant = 'primary' | 'secondary' | 'neutral' | 'danger'
type ButtonSize = 'sm' | 'md' | 'lg'

type ButtonVariantOptions = {
    variant?: ButtonVariant
    size?: ButtonSize
    className?: string
}

const baseClass =
    'rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed'

const variantClassMap: Record<ButtonVariant, string> = {
    primary: 'bg-slate-900 text-white hover:bg-slate-800',
    secondary: 'bg-blue-600 text-white hover:bg-blue-700',
    neutral: 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50',
    danger: 'bg-red-600 text-white hover:bg-red-700',
}

const sizeClassMap: Record<ButtonSize, string> = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-sm',
    lg: 'px-5 py-2.5',
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function buttonVariants(options: ButtonVariantOptions = {}) {
    const { variant = 'primary', size = 'md', className } = options

    return joinClasses(baseClass, variantClassMap[variant], sizeClassMap[size], className)
}

export type { ButtonVariant, ButtonSize, ButtonVariantOptions }

