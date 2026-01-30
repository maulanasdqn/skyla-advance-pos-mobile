package com.skyla.pos.payments.di

import com.skyla.pos.payments.data.api.PaymentApiService
import com.skyla.pos.payments.data.repository.PaymentRepositoryImpl
import com.skyla.pos.payments.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(paymentApiService: PaymentApiService): PaymentRepository {
        return PaymentRepositoryImpl(paymentApiService)
    }
}
