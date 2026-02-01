package com.skyla.pos.dashboard.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.dashboard.data.dto.DashboardSummaryDto

interface DashboardRepository {

    suspend fun getDashboardSummary(): Resource<DashboardSummaryDto>
}
