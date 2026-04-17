import { useState } from 'react'
import { useNavigate, Link } from 'react-router'
import { login } from '../api/authApi'
import { Card } from '../components/ui'
import { buttonVariants } from '../components/ui'
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
        <div className="min-h-screen bg-gradient-to-b from-slate-50 to-slate-100 flex items-center justify-center p-4">
            <Card className="w-full max-w-md p-6">
                <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-2">Help Desk Login</h1>
                <p className="text-sm text-slate-600 mb-6">Sign in to access your dashboard</p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
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

                <p className="text-sm text-slate-600 mt-4">
                    Don&apos;t have an account?{' '}
                    <Link to="/register" className="text-blue-600 hover:underline">
                        Register
                    </Link>
                </p>
            </Card>
        </div>
    )
}