export type UserRole = 'CLIENT' | 'AGENT' | 'ADMIN'

export interface AuthResponse {
    id: number
    fullName: string
    email: string
    role: UserRole
    token?: string
}

export interface CurrentUserResponse extends Omit<AuthResponse, 'token'> {}

export interface LoginRequest {
    email: string
    password: string
}

export interface RegisterRequest {
    fullName: string
    email: string
    password: string
}