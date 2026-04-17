export const API_BASE_URL = import.meta.env.VITE_API_URL

export async function apiFetch<T>(
    path: string,
    options: RequestInit = {}
): Promise<T> {
    const token = localStorage.getItem('token')

    const headers = new Headers(options.headers || {})
    headers.set('Content-Type', 'application/json')

    if (token) {
        headers.set('Authorization', `Bearer ${token}`)
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers,
    })

    const contentType = response.headers.get('content-type') || ''

    let responseData: unknown
    if (contentType.includes('application/json')) {
        responseData = await response.json()
    } else {
        responseData = await response.text()
    }

    if (!response.ok) {
        if (
            responseData &&
            typeof responseData === 'object' &&
            'message' in responseData &&
            typeof responseData.message === 'string'
        ) {
            throw new Error(responseData.message)
        }

        if (typeof responseData === 'string' && responseData.trim() !== '') {
            throw new Error(responseData)
        }

        throw new Error(`Request failed with status ${response.status}`)
    }

    return responseData as T
}