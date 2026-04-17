import { apiFetch } from './client'
import type { UserResponse } from '../types/user'

export function getUsers() {
    return apiFetch<UserResponse[]>('/api/users')
}