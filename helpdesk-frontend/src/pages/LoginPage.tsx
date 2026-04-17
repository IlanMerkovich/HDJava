import { useState } from 'react'
import { useNavigate, Link } from 'react-router'
import { login } from '../api/authApi'
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
        <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
            <div className="w-full max-w-md rounded-2xl bg-white shadow-lg p-6">
                <h1 className="text-2xl font-bold mb-2">Help Desk Login</h1>
                <p className="text-sm text-slate-600 mb-6">
                    Sign in to access your dashboard
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
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
                            className="w-full rounded-lg border px-3 py-2 outline-none focus:ring"
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
                        className="w-full rounded-lg bg-slate-900 text-white py-2.5 font-medium disabled:opacity-50"
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
            </div>
        </div>
    )
}