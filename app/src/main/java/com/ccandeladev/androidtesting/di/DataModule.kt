package com.ccandeladev.androidtesting.di

import com.ccandeladev.androidtesting.core.data.coroutines.DefaultDispatchersProvider
import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.repository.ProductRepositoryImpl
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(defaultDispatchersProvider: DefaultDispatchersProvider): DispatchersProvider {
        return defaultDispatchersProvider
    }

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository {
        return productRepositoryImpl
    }
}
