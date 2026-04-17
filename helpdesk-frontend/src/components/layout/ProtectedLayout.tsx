import { Navigate, Outlet } from 'react-router'
import { useAuth } from '../../context/AuthContext'
import AppNavbar from './AppNavbar'

export default function ProtectedLayout() {
    const { token, isRestoringAuth } = useAuth()

    if (isRestoringAuth) {
        return (
            <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
                <div className="rounded-2xl bg-white shadow-lg px-6 py-4 text-slate-700">
                    Checking your session...
                </div>
            </div>
        )
    }

    if (!token) {
        return <Navigate to="/login" replace />
    }

    return (
        <div className="min-h-screen bg-slate-100">
            <AppNavbar />
            <main className="p-6">
                <Outlet />
            </main>
        </div>
    )
}