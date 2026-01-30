package com.skyla.pos.categories.di

import com.skyla.pos.categories.data.api.CategoryApiService
import com.skyla.pos.categories.data.repository.CategoryRepositoryImpl
import com.skyla.pos.categories.domain.repository.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository

    companion object {

        @Provides
        @Singleton
        fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService {
            return retrofit.create(CategoryApiService::class.java)
        }
    }
}
