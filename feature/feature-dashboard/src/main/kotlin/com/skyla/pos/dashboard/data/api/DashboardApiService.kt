package com.skyla.pos.dashboard.data.api

import com.skyla.pos.dashboard.data.dto.DashboardSummaryDto
import com.skyla.pos.network.dto.SingleResponse
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApiService {

    @GET("dashboard/summary")
    suspend fun getDashboardSummary(): Response<SingleResponse<DashboardSummaryDto>>
}
