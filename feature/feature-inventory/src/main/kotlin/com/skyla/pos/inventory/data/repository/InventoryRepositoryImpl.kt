package com.skyla.pos.inventory.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.inventory.data.api.InventoryApiService
import com.skyla.pos.inventory.data.dto.CreateAdjustmentRequest
import com.skyla.pos.inventory.domain.repository.InventoryRepository
import com.skyla.pos.model.InventoryAdjustment
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.StockLevel
import com.skyla.pos.network.safeApiCall
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryApiService: InventoryApiService,
) : InventoryRepository {

    override suspend fun createAdjustment(
        productId: String,
        quantityChange: Int,
        reason: String,
        referenceId: String?,
        notes: String?,
        adjustedBy: String,
    ): Resource<InventoryAdjustment> {
        val request = CreateAdjustmentRequest(
            productId = productId,
            quantityChange = quantityChange,
            reason = reason,
            referenceId = referenceId,
            notes = notes,
            adjustedBy = adjustedBy,
        )
        val result = safeApiCall { inventoryApiService.createAdjustment(request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getAdjustments(
        page: Int,
        perPage: Int,
    ): Resource<Pair<List<InventoryAdjustment>, PaginationMeta>> {
        val result = safeApiCall { inventoryApiService.getAdjustments(page, perPage) }
        return when (result) {
            is Resource.Success -> {
                val meta = result.data.meta
                val paginationMeta = PaginationMeta(
                    currentPage = meta.currentPage,
                    perPage = meta.perPage,
                    totalItems = meta.totalItems,
                    totalPages = meta.totalPages,
                )
                Resource.Success(result.data.data to paginationMeta)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getAdjustment(id: String): Resource<InventoryAdjustment> {
        val result = safeApiCall { inventoryApiService.getAdjustment(id) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getStockLevel(productId: String): Resource<StockLevel> {
        val result = safeApiCall { inventoryApiService.getStockLevel(productId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getLowStockProducts(): Resource<List<StockLevel>> {
        val result = safeApiCall { inventoryApiService.getLowStockProducts() }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
