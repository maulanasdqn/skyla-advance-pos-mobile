package com.skyla.pos.users.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.model.User
import com.skyla.pos.users.data.dto.CreateUserRequest
import com.skyla.pos.users.data.dto.UpdateUserRequest

interface UserRepository {

    suspend fun getUsers(
        page: Int,
        perPage: Int,
        search: String? = null,
    ): Resource<Pair<List<User>, PaginationMeta>>

    suspend fun getUser(id: String): Resource<User>

    suspend fun createUser(request: CreateUserRequest): Resource<User>

    suspend fun updateUser(id: String, request: UpdateUserRequest): Resource<User>

    suspend fun deleteUser(id: String): Resource<String>

    suspend fun deactivateUser(id: String): Resource<String>
}
