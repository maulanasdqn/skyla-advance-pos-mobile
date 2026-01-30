package com.skyla.pos.products.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.Product
import com.skyla.pos.products.data.dto.CreateProductRequest
import com.skyla.pos.products.data.dto.UpdateProductRequest

interface ProductRepository {

    suspend fun getProducts(
        page: Int,
        perPage: Int,
        search: String? = null,
    ): Resource<Pair<List<Product>, PaginationMeta>>

    suspend fun getProduct(id: String): Resource<Product>

    suspend fun createProduct(request: CreateProductRequest): Resource<Product>

    suspend fun updateProduct(id: String, request: UpdateProductRequest): Resource<Product>

    suspend fun deleteProduct(id: String): Resource<String>

    suspend fun getProductByBarcode(code: String): Resource<Product>

    suspend fun deactivateProduct(id: String): Resource<String>
}
