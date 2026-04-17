import { Navigate, Outlet } from 'react-router'
import { useAuth } from '../../context/AuthContext'
import AppNavbar from './AppNavbar'

export default function ProtectedLayout() {
    const { token, isRestoringAuth } = useAuth()

    if (isRestoringAuth) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-slate-50 via-slate-50 to-slate-100 p-6">
                <div className="rounded-2xl border border-slate-200 bg-white px-6 py-4 text-slate-700 shadow-sm">
                    Checking your session...
                </div>
            </div>
        )
    }

    if (!token) {
        return <Navigate to="/login" replace />
    }

    return (
        <div className="relative min-h-screen overflow-x-clip bg-gradient-to-b from-slate-50 via-slate-50 to-slate-100">
            <div
                aria-hidden="true"
                className="pointer-events-none absolute inset-x-0 top-0 h-72 bg-[radial-gradient(1100px_380px_at_50%_-80px,rgba(148,163,184,0.24),transparent)]"
            />

            <div className="relative">
                <AppNavbar />
                <main className="px-4 py-6 sm:px-6 lg:py-8">
                    <div className="mx-auto w-full max-w-7xl">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    )
}