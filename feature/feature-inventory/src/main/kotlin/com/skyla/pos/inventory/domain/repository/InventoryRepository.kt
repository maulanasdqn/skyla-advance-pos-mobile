package com.skyla.pos.inventory.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.InventoryAdjustment
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.StockLevel

interface InventoryRepository {

    suspend fun createAdjustment(
        productId: String,
        quantityChange: Int,
        reason: String,
        referenceId: String?,
        notes: String?,
        adjustedBy: String,
    ): Resource<InventoryAdjustment>

    suspend fun getAdjustments(
        page: Int,
        perPage: Int,
    ): Resource<Pair<List<InventoryAdjustment>, PaginationMeta>>

    suspend fun getAdjustment(id: String): Resource<InventoryAdjustment>

    suspend fun getStockLevel(productId: String): Resource<StockLevel>

    suspend fun getLowStockProducts(): Resource<List<StockLevel>>
}
