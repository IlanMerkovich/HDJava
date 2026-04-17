import { apiFetch } from './client'
import type { DashboardSummaryResponse } from '../types/dashboard'

const DASHBOARD_SUMMARY_PATH = '/api/dashboard/summary'

export function getDashboardSummary() {
    return apiFetch<DashboardSummaryResponse>(DASHBOARD_SUMMARY_PATH)
}