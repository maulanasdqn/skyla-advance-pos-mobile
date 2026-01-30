package com.skyla.pos.customers.data.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.customers.data.api.CustomerApiService
import com.skyla.pos.customers.data.dto.CreateCustomerRequest
import com.skyla.pos.customers.data.dto.UpdateCustomerRequest
import com.skyla.pos.customers.domain.repository.CustomerRepository
import com.skyla.pos.model.Customer
import com.skyla.pos.model.PaginationMeta
import com.skyla.pos.network.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val apiService: CustomerApiService,
) : CustomerRepository {

    override suspend fun getCustomers(
        page: Int,
        perPage: Int,
        search: String?,
    ): Resource<Pair<List<Customer>, PaginationMeta>> {
        return when (val result = safeApiCall { apiService.getCustomers(page, perPage, search) }) {
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

    override suspend fun getCustomer(id: String): Resource<Customer> {
        return when (val result = safeApiCall { apiService.getCustomer(id) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun createCustomer(request: CreateCustomerRequest): Resource<Customer> {
        return when (val result = safeApiCall { apiService.createCustomer(request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateCustomer(id: String, request: UpdateCustomerRequest): Resource<Customer> {
        return when (val result = safeApiCall { apiService.updateCustomer(id, request) }) {
            is Resource.Success -> Resource.Success(result.data.data)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteCustomer(id: String): Resource<String> {
        return when (val result = safeApiCall { apiService.deleteCustomer(id) }) {
            is Resource.Success -> Resource.Success(result.data.message)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
