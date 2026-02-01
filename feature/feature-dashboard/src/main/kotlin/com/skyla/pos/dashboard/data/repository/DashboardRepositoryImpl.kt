package com.skyla.pos.dashboard.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.dashboard.data.api.DashboardApiService
import com.skyla.pos.dashboard.data.dto.DashboardSummaryDto
import com.skyla.pos.dashboard.domain.repository.DashboardRepository
import com.skyla.pos.network.safeApiCall
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardApiService: DashboardApiService,
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Resource<DashboardSummaryDto> {
        val result = safeApiCall { dashboardApiService.getDashboardSummary() }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
