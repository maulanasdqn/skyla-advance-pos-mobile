package com.skyla.pos.inventory.di

import com.skyla.pos.inventory.data.api.InventoryApiService
import com.skyla.pos.inventory.data.repository.InventoryRepositoryImpl
import com.skyla.pos.inventory.domain.repository.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {

    @Provides
    @Singleton
    fun provideInventoryApiService(retrofit: Retrofit): InventoryApiService {
        return retrofit.create(InventoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(inventoryApiService: InventoryApiService): InventoryRepository {
        return InventoryRepositoryImpl(inventoryApiService)
    }
}
