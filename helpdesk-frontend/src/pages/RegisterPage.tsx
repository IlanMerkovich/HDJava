import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { register } from '../api/authApi'
import { BrandLogo, Card, buttonVariants, formControlVariants, useToast } from '../components/ui'
import { useAuth } from '../context/AuthContext'

export default function RegisterPage() {
    const navigate = useNavigate()
    const { login: saveLogin } = useAuth()
    const toast = useToast()

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
            toast.success('Account created successfully')
            navigate('/dashboard')
        } catch (err) {
            console.error('Register error:', err)

            if (err instanceof Error) {
                setError(err.message)
                toast.error(err.message)
            } else {
                setError('Register failed')
                toast.error('Register failed')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-slate-50 via-slate-50 to-slate-100 p-4">
            <Card className="w-full max-w-md p-6 sm:p-7">
                <div className="mb-6 space-y-2">
                    <BrandLogo size="md" className="mb-2" />
                    <h1 className="text-2xl font-bold tracking-tight text-slate-900">Create account</h1>
                    <p className="text-sm text-slate-600">Register to access the Help Desk system</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">Full Name</label>
                        <input
                            type="text"
                            className={formControlVariants()}
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                            placeholder="Your full name"
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">Email</label>
                        <input
                            type="email"
                            className={formControlVariants()}
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium text-slate-700">Password</label>
                        <input
                            type="password"
                            className={formControlVariants()}
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

                <p className="mt-5 text-sm text-slate-600">
                    Already have an account?{' '}
                    <Link to="/login" className="text-blue-600 hover:underline">
                        Login
                    </Link>
                </p>
            </Card>
        </div>
    )
}