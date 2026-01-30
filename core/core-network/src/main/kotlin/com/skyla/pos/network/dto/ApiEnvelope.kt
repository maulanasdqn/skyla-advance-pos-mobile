package com.skyla.pos.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleResponse<T>(
    val data: T
)

@Serializable
data class ListResponse<T>(
    val data: List<T>,
    val meta: PaginationMetaDto
)

@Serializable
data class PaginationMetaDto(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("total_items") val totalItems: Long,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class ErrorResponse(
    val message: String
)

@Serializable
data class MessageResponse(
    val message: String
)
