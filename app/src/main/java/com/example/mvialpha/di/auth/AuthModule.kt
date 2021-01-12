package com.example.mvialpha.di.auth

import AccountPropertiesDao
import AuthTokenDao
import android.content.SharedPreferences
import com.example.mvialpha.api.auth.OpenApiAuthService

import com.example.mvialpha.repository.auth.AuthRepository
import com.example.mvialpha.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            editor
        )
    }

}