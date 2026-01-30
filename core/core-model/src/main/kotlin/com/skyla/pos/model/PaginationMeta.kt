package com.skyla.pos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationMeta(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_items") val totalItems: Long,
    @SerialName("total_pages") val totalPages: Int
)
