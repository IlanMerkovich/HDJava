import { Navigate, Outlet } from 'react-router'
import { useAuth } from '../../context/AuthContext'
import AppNavbar from './AppNavbar'

export default function ProtectedLayout() {
    const { token, isRestoringAuth } = useAuth()

    if (isRestoringAuth) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-slate-50 to-slate-100 flex items-center justify-center p-6">
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
        <div className="min-h-screen bg-gradient-to-b from-slate-50 to-slate-100">
            <AppNavbar />
            <main className="px-4 py-6 sm:px-6 lg:py-8">
                <div className="mx-auto w-full max-w-7xl">
                    <Outlet />
                </div>
            </main>
        </div>
    )
}