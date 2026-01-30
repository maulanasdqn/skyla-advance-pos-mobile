package com.skyla.pos.sales.di

import com.skyla.pos.sales.data.api.SaleApiService
import com.skyla.pos.sales.data.repository.SaleRepositoryImpl
import com.skyla.pos.sales.domain.repository.SaleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SaleModule {

    @Provides
    @Singleton
    fun provideSaleApiService(retrofit: Retrofit): SaleApiService {
        return retrofit.create(SaleApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSaleRepository(saleApiService: SaleApiService): SaleRepository {
        return SaleRepositoryImpl(saleApiService)
    }
}
