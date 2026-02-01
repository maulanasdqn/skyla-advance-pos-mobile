package com.skyla.pos.users.di

import com.skyla.pos.users.data.api.UserApiService
import com.skyla.pos.users.data.repository.UserRepositoryImpl
import com.skyla.pos.users.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl,
    ): UserRepository

    companion object {

        @Provides
        @Singleton
        fun provideUserApiService(retrofit: Retrofit): UserApiService {
            return retrofit.create(UserApiService::class.java)
        }
    }
}
