package com.skyla.pos.products.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.Product
import com.skyla.pos.network.safeApiCall
import com.skyla.pos.products.data.api.ProductApiService
import com.skyla.pos.products.data.dto.CreateProductRequest
import com.skyla.pos.products.data.dto.UpdateProductRequest
import com.skyla.pos.products.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService,
) : ProductRepository {

    override suspend fun getProducts(
        page: Int,
        perPage: Int,
        search: String?,
    ): Resource<Pair<List<Product>, PaginationMeta>> {
        return when (val result = safeApiCall { apiService.getProducts(page, perPage, search) }) {
            is Resource.Success -> {
                val body = result.data
                val meta = PaginationMeta(
                    currentPage = body.meta.currentPage,
                    perPage = body.meta.perPage,
                    totalItems = body.meta.totalItems,
                    totalPages = body.meta.totalPages,
                )
                Resource.Success(Pair(body.data, meta))
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getProduct(id: String): Resource<Product> {
        return when (val result = safeApiCall { apiService.getProduct(id) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun createProduct(request: CreateProductRequest): Resource<Product> {
        return when (val result = safeApiCall { apiService.createProduct(request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateProduct(id: String, request: UpdateProductRequest): Resource<Product> {
        return when (val result = safeApiCall { apiService.updateProduct(id, request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteProduct(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deleteProduct(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getProductByBarcode(code: String): Resource<Product> {
        return when (val result = safeApiCall { apiService.getProductByBarcode(code) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deactivateProduct(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deactivateProduct(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
