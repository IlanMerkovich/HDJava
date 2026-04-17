export type UserRole = 'CLIENT' | 'AGENT' | 'ADMIN'

export interface UserResponse {
    id: number
    fullName: string
    email: string
    role: UserRole
}