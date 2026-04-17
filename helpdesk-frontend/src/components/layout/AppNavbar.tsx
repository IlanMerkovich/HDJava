import { Link, useLocation, useNavigate } from 'react-router'
import { useQuery } from '@tanstack/react-query'
import { useAuth } from '../../context/AuthContext'
import { getNotifications } from '../../api/notificationApi'

function navClass(isActive: boolean) {
    return isActive
        ? 'rounded-lg bg-slate-900 text-white px-3 py-2 text-sm font-medium'
        : 'rounded-lg text-slate-700 hover:bg-slate-100 px-3 py-2 text-sm font-medium'
}

export default function AppNavbar() {
    const location = useLocation()
    const navigate = useNavigate()
    const { user, logout } = useAuth()

    const { data: notifications } = useQuery({
        queryKey: ['notifications'],
        queryFn: getNotifications,
    })

    const unreadCount = (notifications ?? []).filter(
        (notification) => !notification.read
    ).length

    function handleLogout() {
        logout()
        navigate('/login')
    }

    return (
        <header className="bg-white border-b">
            <div className="max-w-7xl mx-auto px-6 py-4 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                <div className="flex items-center gap-4">
                    <Link to="/dashboard" className="text-2xl font-bold text-slate-900">
                        Help Desk
                    </Link>

                    <nav className="flex flex-wrap items-center gap-2">
                        <Link
                            to="/dashboard"
                            className={navClass(location.pathname === '/dashboard')}
                        >
                            Dashboard
                        </Link>

                        <Link
                            to="/tickets"
                            className={navClass(location.pathname === '/tickets')}
                        >
                            Tickets
                        </Link>

                        <Link
                            to="/tickets/new"
                            className={navClass(location.pathname === '/tickets/new')}
                        >
                            Create Ticket
                        </Link>

                        <Link
                            to="/notifications"
                            className={`${navClass(location.pathname === '/notifications')} inline-flex items-center gap-2`}
                        >
                            <span>Notifications</span>

                            {unreadCount > 0 && (
                                <span className="min-w-[22px] h-[22px] rounded-full bg-red-600 text-white text-xs font-bold flex items-center justify-center px-1">
                  {unreadCount}
                </span>
                            )}
                        </Link>
                    </nav>
                </div>

                <div className="flex flex-wrap items-center gap-3">
                    <div className="text-right">
                        <p className="text-sm font-semibold text-slate-900">
                            {user?.fullName ?? '-'}
                        </p>
                        <p className="text-xs text-slate-500">
                            {user?.email ?? '-'} · {user?.role ?? '-'}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={handleLogout}
                        className="rounded-lg bg-red-600 text-white px-4 py-2 text-sm font-medium"
                    >
                        Logout
                    </button>
                </div>
            </div>
        </header>
    )
}