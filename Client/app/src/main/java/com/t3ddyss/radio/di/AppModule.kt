package com.t3ddyss.radio.di

import com.t3ddyss.radio.api.RadioService
import com.t3ddyss.radio.utilities.getBaseUrlForCurrentDevice
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBaseUrlForCurrentDevice())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideRadioService(retrofit: Retrofit): RadioService {
        return retrofit.create(RadioService::class.java)
    }
}