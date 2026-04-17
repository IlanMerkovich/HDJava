import { useEffect } from 'react'
import type { MouseEvent } from 'react'
import { buttonVariants } from './buttonVariants'

type ConfirmDialogProps = {
    open: boolean
    title: string
    message: string
    confirmLabel?: string
    cancelLabel?: string
    isPending?: boolean
    onConfirm: () => void
    onCancel: () => void
}

export function ConfirmDialog({
    open,
    title,
    message,
    confirmLabel = 'Confirm',
    cancelLabel = 'Cancel',
    isPending = false,
    onConfirm,
    onCancel,
}: ConfirmDialogProps) {
    useEffect(() => {
        function handleKeyDown(event: KeyboardEvent) {
            if (event.key === 'Escape') {
                onCancel()
            }
        }

        if (open) {
            window.addEventListener('keydown', handleKeyDown)
        }

        return () => {
            window.removeEventListener('keydown', handleKeyDown)
        }
    }, [open, onCancel])

    if (!open) {
        return null
    }

    return (
        <div
            role="presentation"
            className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"
            onMouseDown={onCancel}
        >
            <div
                className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
                onMouseDown={(event: MouseEvent<HTMLDivElement>) => event.stopPropagation()}
            >
                <div role="alertdialog" aria-modal="true" aria-labelledby="confirm-dialog-title" aria-describedby="confirm-dialog-message" className="space-y-4">
                    <div>
                        <h3 id="confirm-dialog-title" className="text-xl font-bold text-slate-900">
                            {title}
                        </h3>
                        <p id="confirm-dialog-message" className="mt-2 text-sm text-slate-600">
                            {message}
                        </p>
                    </div>

                    <div className="flex flex-wrap justify-end gap-3">
                        <button
                            type="button"
                            onClick={onCancel}
                            className={buttonVariants({ variant: 'neutral' })}
                            disabled={isPending}
                        >
                            {cancelLabel}
                        </button>

                        <button
                            type="button"
                            onClick={onConfirm}
                            className={buttonVariants({ variant: 'danger' })}
                            disabled={isPending}
                        >
                            {isPending ? 'Working...' : confirmLabel}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

