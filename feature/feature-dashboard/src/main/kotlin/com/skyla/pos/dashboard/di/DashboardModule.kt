package com.skyla.pos.dashboard.di

import com.skyla.pos.dashboard.data.api.DashboardApiService
import com.skyla.pos.dashboard.data.repository.DashboardRepositoryImpl
import com.skyla.pos.dashboard.domain.repository.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService =
        retrofit.create(DashboardApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DashboardBindingModule {

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository
}
