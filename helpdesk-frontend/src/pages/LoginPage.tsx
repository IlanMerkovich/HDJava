import { useState } from 'react'
import { useNavigate, Link } from 'react-router'
import { login } from '../api/authApi'
import { Card, buttonVariants, formControlVariants } from '../components/ui'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
    const navigate = useNavigate()
    const { login: saveLogin } = useAuth()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()
        setError('')
        setLoading(true)

        try {
            const authResponse = await login({ email, password })
            saveLogin(authResponse)
            navigate('/dashboard')
        }
        catch (err) {
            console.error('Login error:', err)

            if (err instanceof Error) {
                setError(err.message)
            } else {
                setError('Login failed')
            }
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-slate-50 via-slate-50 to-slate-100 p-4">
            <Card className="w-full max-w-md p-6 sm:p-7">
                <div className="mb-6 space-y-2">
                    <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Help Desk</p>
                    <h1 className="text-2xl font-bold tracking-tight text-slate-900">Welcome back</h1>
                    <p className="text-sm text-slate-600">Sign in to access your dashboard</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">Email</label>
                        <input
                            type="email"
                            className={formControlVariants()}
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">Password</label>
                        <input
                            type="password"
                            className={formControlVariants()}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter your password"
                            required
                        />
                    </div>

                    {error && (
                        <div className="rounded-lg bg-red-100 text-red-700 px-3 py-2 text-sm">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className={buttonVariants({ size: 'lg', className: 'w-full' })}
                    >
                        {loading ? 'Signing in...' : 'Login'}
                    </button>
                </form>

                <p className="mt-5 text-sm text-slate-600">
                    Don&apos;t have an account?{' '}
                    <Link to="/register" className="text-blue-600 hover:underline">
                        Register
                    </Link>
                </p>
            </Card>
        </div>
    )
}