import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { register } from '../api/authApi'
import { Card } from '../components/ui'
import { buttonVariants } from '../components/ui'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
    const navigate = useNavigate()
    const { login: saveLogin } = useAuth()

    const [fullName, setFullName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault()

        if (!fullName.trim()) {
            setError('Full name is required')
            return
        }

        if (!email.trim()) {
            setError('Email is required')
            return
        }

        if (!password.trim()) {
            setError('Password is required')
            return
        }

        setError('')
        setLoading(true)

        try {
            const authResponse = await register({
                fullName,
                email,
                password,
            })

            saveLogin(authResponse)
            navigate('/dashboard')
        } catch (err) {
            console.error('Register error:', err)

            if (err instanceof Error) {
                setError(err.message)
            } else {
                setError('Register failed')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-slate-50 to-slate-100 flex items-center justify-center p-4">
            <Card className="w-full max-w-md p-6">
                <h1 className="text-2xl font-bold tracking-tight text-slate-900 mb-2">Create Account</h1>
                <p className="text-sm text-slate-600 mb-6">
                    Register to access the Help Desk system
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Full Name</label>
                        <input
                            type="text"
                            className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            placeholder="Your full name"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Password</label>
                        <input
                            type="password"
                            className="w-full rounded-lg border border-slate-300 px-3 py-2 outline-none transition-colors focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Choose a password"
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
                        {loading ? 'Creating Account...' : 'Register'}
                    </button>
                </form>

                <p className="text-sm text-slate-600 mt-4">
                    Already have an account?{' '}
                    <Link to="/login" className="text-blue-600 hover:underline">
                        Login
                    </Link>
                </p>
            </Card>
        </div>
    )
}