package com.skyla.pos.sales.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.Sale
import com.skyla.pos.network.safeApiCall
import com.skyla.pos.sales.data.api.SaleApiService
import com.skyla.pos.sales.data.dto.AddSaleItemRequest
import com.skyla.pos.sales.data.dto.ApplyDiscountRequest
import com.skyla.pos.sales.data.dto.CreateSaleRequest
import com.skyla.pos.sales.data.dto.ReceiptResponse
import com.skyla.pos.sales.data.dto.UpdateSaleItemRequest
import com.skyla.pos.sales.data.dto.VoidSaleRequest
import com.skyla.pos.sales.domain.repository.SaleRepository
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleApiService: SaleApiService,
) : SaleRepository {

    override suspend fun createSale(customerId: String?): Resource<Sale> {
        val result = safeApiCall { saleApiService.createSale(CreateSaleRequest(customerId)) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getSales(
        page: Int,
        perPage: Int,
        status: String?,
    ): Resource<Pair<List<Sale>, PaginationMeta>> {
        val result = safeApiCall { saleApiService.getSales(page, perPage, status) }
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

    override suspend fun getSale(id: String): Resource<Sale> {
        val result = safeApiCall { saleApiService.getSale(id) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun addSaleItem(
        saleId: String,
        productId: String,
        quantity: Int,
        discountAmount: Long?,
    ): Resource<Sale> {
        val request = AddSaleItemRequest(
            productId = productId,
            quantity = quantity,
            discountAmount = discountAmount,
        )
        val result = safeApiCall { saleApiService.addSaleItem(saleId, request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateSaleItem(
        saleId: String,
        itemId: String,
        quantity: Int?,
        discountAmount: Long?,
    ): Resource<Sale> {
        val request = UpdateSaleItemRequest(
            quantity = quantity,
            discountAmount = discountAmount,
        )
        val result = safeApiCall { saleApiService.updateSaleItem(saleId, itemId, request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun removeSaleItem(saleId: String, itemId: String): Resource<Sale> {
        val result = safeApiCall { saleApiService.removeSaleItem(saleId, itemId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun applyDiscount(saleId: String, discountAmount: Long): Resource<Sale> {
        val request = ApplyDiscountRequest(discountAmount = discountAmount)
        val result = safeApiCall { saleApiService.applyDiscount(saleId, request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun completeSale(saleId: String): Resource<Sale> {
        val result = safeApiCall { saleApiService.completeSale(saleId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun voidSale(saleId: String, reason: String): Resource<Sale> {
        val request = VoidSaleRequest(reason = reason)
        val result = safeApiCall { saleApiService.voidSale(saleId, request) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getReceipt(saleId: String): Resource<ReceiptResponse> {
        val result = safeApiCall { saleApiService.getReceipt(saleId) }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
