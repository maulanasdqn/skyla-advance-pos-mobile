package com.skyla.pos.categories.data.repository

import com.skyla.pos.categories.data.api.CategoryApiService
import com.skyla.pos.categories.data.dto.CreateCategoryRequest
import com.skyla.pos.categories.data.dto.UpdateCategoryRequest
import com.skyla.pos.categories.domain.repository.CategoryRepository
import com.skyla.pos.common.Resource
import com.skyla.pos.model.Category
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val apiService: CategoryApiService,
) : CategoryRepository {

    override suspend fun getCategories(
        page: Int,
        perPage: Int,
        search: String?,
    ): Resource<Pair<List<Category>, PaginationMeta>> {
        return when (val result = safeApiCall { apiService.getCategories(page, perPage, search) }) {
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

    override suspend fun getCategory(id: String): Resource<Category> {
        return when (val result = safeApiCall { apiService.getCategory(id) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun createCategory(request: CreateCategoryRequest): Resource<Category> {
        return when (val result = safeApiCall { apiService.createCategory(request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateCategory(id: String, request: UpdateCategoryRequest): Resource<Category> {
        return when (val result = safeApiCall { apiService.updateCategory(id, request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteCategory(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deleteCategory(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deactivateCategory(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deactivateCategory(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
