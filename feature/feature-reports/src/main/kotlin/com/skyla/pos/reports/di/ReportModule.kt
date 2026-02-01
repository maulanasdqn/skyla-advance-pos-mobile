package com.skyla.pos.reports.di

import com.skyla.pos.reports.data.api.ReportApiService
import com.skyla.pos.reports.data.repository.ReportRepositoryImpl
import com.skyla.pos.reports.domain.repository.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl,
    ): ReportRepository

    companion object {

        @Provides
        @Singleton
        fun provideReportApiService(retrofit: Retrofit): ReportApiService {
            return retrofit.create(ReportApiService::class.java)
        }
    }
}
