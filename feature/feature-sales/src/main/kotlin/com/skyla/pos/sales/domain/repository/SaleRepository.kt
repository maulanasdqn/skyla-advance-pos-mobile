package com.skyla.pos.sales.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.Sale
import com.skyla.pos.sales.data.dto.ReceiptResponse

interface SaleRepository {

    suspend fun createSale(customerId: String?): Resource<Sale>

    suspend fun getSales(
        page: Int,
        perPage: Int,
        status: String?,
    ): Resource<Pair<List<Sale>, PaginationMeta>>

    suspend fun getSale(id: String): Resource<Sale>

    suspend fun addSaleItem(
        saleId: String,
        productId: String,
        quantity: Int,
        discountAmount: Long?,
    ): Resource<Sale>

    suspend fun updateSaleItem(
        saleId: String,
        itemId: String,
        quantity: Int?,
        discountAmount: Long?,
    ): Resource<Sale>

    suspend fun removeSaleItem(saleId: String, itemId: String): Resource<Sale>

    suspend fun applyDiscount(saleId: String, discountAmount: Long): Resource<Sale>

    suspend fun completeSale(saleId: String): Resource<Sale>

    suspend fun voidSale(saleId: String, reason: String): Resource<Sale>

    suspend fun getReceipt(saleId: String): Resource<ReceiptResponse>
}
