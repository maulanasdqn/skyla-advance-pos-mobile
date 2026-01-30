package com.skyla.pos.customers.domain.repository

import com.skyla.pos.common.Resource
import com.skyla.pos.customers.data.dto.CreateCustomerRequest
import com.skyla.pos.customers.data.dto.UpdateCustomerRequest
import com.skyla.pos.model.Customer
import com.skyla.pos.model.PaginationMeta

interface CustomerRepository {

    suspend fun getCustomers(
        page: Int,
        perPage: Int,
        search: String? = null,
    ): Resource<Pair<List<Customer>, PaginationMeta>>

    suspend fun getCustomer(id: String): Resource<Customer>

    suspend fun createCustomer(request: CreateCustomerRequest): Resource<Customer>

    suspend fun updateCustomer(id: String, request: UpdateCustomerRequest): Resource<Customer>

    suspend fun deleteCustomer(id: String): Resource<String>
}
