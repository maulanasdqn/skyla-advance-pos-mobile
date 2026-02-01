package com.skyla.pos.users.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.User
import com.skyla.pos.network.safeApiCall
import com.skyla.pos.users.data.api.UserApiService
import com.skyla.pos.users.data.dto.CreateUserRequest
import com.skyla.pos.users.data.dto.UpdateUserRequest
import com.skyla.pos.users.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
) : UserRepository {

    override suspend fun getUsers(
        page: Int,
        perPage: Int,
        search: String?,
    ): Resource<Pair<List<User>, PaginationMeta>> {
        return when (val result = safeApiCall { apiService.getUsers(page, perPage, search) }) {
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

    override suspend fun getUser(id: String): Resource<User> {
        return when (val result = safeApiCall { apiService.getUser(id) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun createUser(request: CreateUserRequest): Resource<User> {
        return when (val result = safeApiCall { apiService.createUser(request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateUser(id: String, request: UpdateUserRequest): Resource<User> {
        return when (val result = safeApiCall { apiService.updateUser(id, request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteUser(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deleteUser(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deactivateUser(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deactivateUser(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
