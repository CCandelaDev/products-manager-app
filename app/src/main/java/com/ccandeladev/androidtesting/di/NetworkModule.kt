package com.ccandeladev.androidtesting.di

import com.ccandeladev.androidtesting.BuildConfig
import com.ccandeladev.androidtesting.productlist.data.remote.ProductManagerApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl") // To provide baseUrl (necessary for testing)
    fun provideBaseUrl(): String {
        return "https://raw.githubusercontent.com/CCandelaDev/productmanager-api/main/"
    }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        // to intercept the logs and display more information
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        // Builder for okHttpClient
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }

        return builder
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    //To provide json for Retrofit
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    //Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        @Named("baseUrl") baseUrl: String  // To provide baseUrl (necessary for testing)
    ): Retrofit {

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType = contentType))
            .build()


    }

    //To provide ApiService
    @Provides
    @Singleton
    fun productManagerApiService(retrofit: Retrofit): ProductManagerApiService {
        return retrofit.create(ProductManagerApiService::class.java)
    }
}

