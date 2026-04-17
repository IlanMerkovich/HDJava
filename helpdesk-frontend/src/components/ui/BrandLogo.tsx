import type { ImgHTMLAttributes } from 'react'

type BrandLogoSize = 'sm' | 'md' | 'lg'
type BrandLogoVariant = 'plain' | 'framed'

type BrandLogoProps = Omit<ImgHTMLAttributes<HTMLImageElement>, 'src'> & {
    size?: BrandLogoSize
    variant?: BrandLogoVariant
}

const sizeClassMap: Record<BrandLogoSize, string> = {
    sm: 'h-8',
    md: 'h-9',
    lg: 'h-9 sm:h-10',
}

const variantClassMap: Record<BrandLogoVariant, string> = {
    plain: '',
    framed: 'rounded-xl bg-white/10 p-1.5 ring-1 ring-white/15',
}

function joinClasses(...classes: Array<string | undefined>) {
    return classes.filter(Boolean).join(' ')
}

export function BrandLogo({ alt = 'Helpdesk Support', size = 'md', variant = 'plain', className, ...props }: BrandLogoProps) {
    return (
        <img
            src="/helpdesk-support-logo.svg"
            alt={alt}
            className={joinClasses('w-auto', sizeClassMap[size], variantClassMap[variant], className)}
            {...props}
        />
    )
}

export type { BrandLogoSize, BrandLogoVariant }

