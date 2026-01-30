package com.skyla.pos.customers.di

import com.skyla.pos.customers.data.api.CustomerApiService
import com.skyla.pos.customers.data.repository.CustomerRepositoryImpl
import com.skyla.pos.customers.domain.repository.CustomerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerModule {

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        impl: CustomerRepositoryImpl,
    ): CustomerRepository

    companion object {

        @Provides
        @Singleton
        fun provideCustomerApiService(retrofit: Retrofit): CustomerApiService {
            return retrofit.create(CustomerApiService::class.java)
        }
    }
}
