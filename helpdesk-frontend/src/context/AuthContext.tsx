import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { getCurrentUser } from '../api/authApi'
import type { AuthResponse, CurrentUserResponse } from '../types/auth'

type AuthContextType = {
    user: AuthResponse | null
    token: string | null
    isRestoringAuth: boolean
    login: (auth: AuthResponse) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

function getStoredUser(): AuthResponse | null {
    const raw = localStorage.getItem('user')
    if (!raw) {
        return null
    }

    try {
        return JSON.parse(raw) as AuthResponse
    } catch {
        return null
    }
}

function getStoredToken(): string | null {
    return localStorage.getItem('token')
}

function clearStoredAuth() {
    localStorage.removeItem('user')
    localStorage.removeItem('token')
}

function saveStoredAuth(user: AuthResponse, token: string) {
    localStorage.setItem('user', JSON.stringify(user))
    localStorage.setItem('token', token)
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [initialUser] = useState<AuthResponse | null>(() => getStoredUser())
    const [initialToken] = useState<string | null>(() => getStoredToken())

    const [user, setUser] = useState<AuthResponse | null>(initialUser)
    const [token, setToken] = useState<string | null>(initialToken)
    const [isRestoringAuth, setIsRestoringAuth] = useState(Boolean(initialToken))
    const [hasCompletedInitialAuthCheck, setHasCompletedInitialAuthCheck] = useState(
        !initialToken
    )

    useEffect(() => {
        if (hasCompletedInitialAuthCheck) {
            return
        }

        if (!token) {
            setIsRestoringAuth(false)
            setHasCompletedInitialAuthCheck(true)
            return
        }

        let cancelled = false

        async function restoreAuth() {
            try {
                const currentUser: CurrentUserResponse = await getCurrentUser()

                if (cancelled) {
                    return
                }

                const nextUser: AuthResponse = initialUser
                    ? {
                          ...initialUser,
                          email: currentUser.email,
                          role: currentUser.role,
                          token: token ?? initialUser.token,
                      }
                    : {
                          id: 0,
                          fullName: currentUser.email,
                          email: currentUser.email,
                          role: currentUser.role,
                          token: token ?? undefined,
                      }

                setUser(nextUser)

                if (token) {
                    saveStoredAuth(nextUser, token)
                }
            } catch {
                if (cancelled) {
                    return
                }

                setUser(null)
                setToken(null)
                clearStoredAuth()
            } finally {
                if (!cancelled) {
                    setIsRestoringAuth(false)
                    setHasCompletedInitialAuthCheck(true)
                }
            }
        }

        void restoreAuth()

        return () => {
            cancelled = true
        }
    }, [hasCompletedInitialAuthCheck, token, initialUser])

    const value = useMemo(
        () => ({
            user,
            token,
            isRestoringAuth,
            login: (auth: AuthResponse) => {
                setUser(auth)
                setToken(auth.token ?? null)

                if (auth.token) {
                    saveStoredAuth(auth, auth.token)
                } else {
                    localStorage.setItem('user', JSON.stringify(auth))
                }

                setIsRestoringAuth(false)
                setHasCompletedInitialAuthCheck(true)
            },
            logout: () => {
                setUser(null)
                setToken(null)
                clearStoredAuth()
                setIsRestoringAuth(false)
                setHasCompletedInitialAuthCheck(true)
            },
        }),
        [isRestoringAuth, user, token]
    )

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) {
        throw new Error('useAuth must be used inside AuthProvider')
    }
    return context
}