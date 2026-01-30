package com.skyla.pos.categories.domain.repository

import com.skyla.pos.categories.data.dto.CreateCategoryRequest
import com.skyla.pos.categories.data.dto.UpdateCategoryRequest
import com.skyla.pos.common.Resource
import com.skyla.pos.model.Category
import com.skyla.pos.model.PaginationMeta

interface CategoryRepository {

    suspend fun getCategories(
        page: Int,
        perPage: Int,
        search: String? = null,
    ): Resource<Pair<List<Category>, PaginationMeta>>

    suspend fun getCategory(id: String): Resource<Category>

    suspend fun createCategory(request: CreateCategoryRequest): Resource<Category>

    suspend fun updateCategory(id: String, request: UpdateCategoryRequest): Resource<Category>

    suspend fun deleteCategory(id: String): Resource<String>

    suspend fun deactivateCategory(id: String): Resource<String>
}
