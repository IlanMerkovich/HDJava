import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'

type ToastVariant = 'success' | 'error' | 'info'

type ToastItem = {
    id: string
    message: string
    variant: ToastVariant
}

type ToastContextValue = {
    showToast: (message: string, variant?: ToastVariant) => void
    success: (message: string) => void
    error: (message: string) => void
    info: (message: string) => void
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined)

const toastClassByVariant: Record<ToastVariant, string> = {
    success: 'border-emerald-200 bg-emerald-50 text-emerald-700',
    error: 'border-red-200 bg-red-50 text-red-700',
    info: 'border-slate-200 bg-white text-slate-700',
}

function createToastId() {
    if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
        return crypto.randomUUID()
    }
    return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

export function ToastProvider({ children }: { children: ReactNode }) {
    const [toasts, setToasts] = useState<ToastItem[]>([])

    const removeToast = useCallback((toastId: string) => {
        setToasts((prev) => prev.filter((toast) => toast.id !== toastId))
    }, [])

    const showToast = useCallback((message: string, variant: ToastVariant = 'info') => {
        const toastId = createToastId()

        setToasts((prev) => [...prev, { id: toastId, message, variant }])

        window.setTimeout(() => {
            removeToast(toastId)
        }, 3500)
    }, [removeToast])

    const value = useMemo<ToastContextValue>(
        () => ({
            showToast,
            success: (message: string) => showToast(message, 'success'),
            error: (message: string) => showToast(message, 'error'),
            info: (message: string) => showToast(message, 'info'),
        }),
        [showToast]
    )

    return (
        <ToastContext.Provider value={value}>
            {children}

            <div className="pointer-events-none fixed right-4 top-4 z-[60] flex w-[min(92vw,360px)] flex-col gap-2" aria-live="polite" aria-atomic="true">
                {toasts.map((toast) => (
                    <div
                        key={toast.id}
                        className={`pointer-events-auto rounded-xl border px-4 py-3 text-sm shadow-sm backdrop-blur ${toastClassByVariant[toast.variant]}`}
                        role="status"
                    >
                        <div className="flex items-start justify-between gap-3">
                            <p className="leading-5">{toast.message}</p>
                            <button
                                type="button"
                                onClick={() => removeToast(toast.id)}
                                className="rounded-md px-1 text-xs font-semibold opacity-70 transition-opacity hover:opacity-100"
                                aria-label="Dismiss notification"
                            >
                                x
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </ToastContext.Provider>
    )
}

export function useToast() {
    const context = useContext(ToastContext)

    if (!context) {
        throw new Error('useToast must be used inside ToastProvider')
    }

    return context
}

export type { ToastVariant }

