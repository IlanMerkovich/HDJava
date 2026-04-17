import { Link, useLocation, useNavigate } from 'react-router'
import { useQuery } from '@tanstack/react-query'
import { buttonVariants } from '../ui'
import { useAuth } from '../../context/AuthContext'
import { getNotifications } from '../../api/notificationApi'

function navClass(isActive: boolean) {
    return isActive
        ? 'rounded-lg bg-slate-900 text-white px-3.5 py-2 text-sm font-semibold shadow-sm'
        : 'rounded-lg text-slate-700 px-3.5 py-2 text-sm font-medium transition-colors hover:bg-slate-100 hover:text-slate-900'
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
        <header className="sticky top-0 z-30 border-b border-slate-200/80 bg-white/90 backdrop-blur">
            <div className="max-w-7xl mx-auto px-4 py-3 sm:px-6">
                <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:gap-4">
                        <Link
                            to="/dashboard"
                            className="text-xl font-bold tracking-tight text-slate-900 sm:text-2xl"
                        >
                            Help Desk
                        </Link>

                        <nav className="flex flex-wrap items-center gap-1.5 rounded-xl border border-slate-200 bg-white p-1.5">
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
                                    <span className="inline-flex min-w-[22px] items-center justify-center rounded-full bg-red-600 px-1.5 py-0.5 text-[11px] font-bold leading-none text-white">
                                        {unreadCount}
                                    </span>
                                )}
                            </Link>
                        </nav>
                    </div>

                    <div className="flex flex-wrap items-center gap-3 sm:justify-end">
                        <div className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-right">
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
                            className={buttonVariants({ variant: 'neutral' })}
                        >
                            Logout
                        </button>
                    </div>
                </div>
            </div>
        </header>
    )
}