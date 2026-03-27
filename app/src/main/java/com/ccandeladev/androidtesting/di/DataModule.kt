package com.ccandeladev.androidtesting.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.ccandeladev.androidtesting.cart.data.local.database.dao.CartItemDao
import com.ccandeladev.androidtesting.core.data.coroutines.DefaultDispatchersProvider
import com.ccandeladev.androidtesting.core.data.local.database.ProductManagerDatabase
import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.OfferDao
import com.ccandeladev.androidtesting.productlist.data.local.database.dao.ProductDao
import com.ccandeladev.androidtesting.productlist.data.repository.OfferRepositoryImpl
import com.ccandeladev.androidtesting.productlist.data.repository.ProductRepositoryImpl
import com.ccandeladev.androidtesting.productlist.data.repository.SettingsRepositoryImpl
import com.ccandeladev.androidtesting.productlist.domain.repository.OfferRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.ProductRepository
import com.ccandeladev.androidtesting.productlist.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//To provide preferences(settings)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    //To provide dispatchers
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

    @Provides
    @Singleton
    fun provideOfferRepository(offerRepositoryImpl: OfferRepositoryImpl): OfferRepository {
        return offerRepositoryImpl
    }

    //use Room.databaseBuilder to create the instance of ProductManagerDatabase. (Inject context)
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): ProductManagerDatabase {
        return Room.databaseBuilder(
            context = context,
            ProductManagerDatabase::class.java,
            "product_manager_db"
        ).build()
    }

    //function that que receive the DB and return db.productDao().
    @Provides
    fun provideProductDao(productManagerDatabase: ProductManagerDatabase): ProductDao {
        return productManagerDatabase.productDao()
    }

    //function that que receive the DB and return db.offerDao().
    @Provides
    fun provideOfferDao(productManagerDatabase: ProductManagerDatabase): OfferDao {
        return productManagerDatabase.offerDao()
    }

    @Provides
    fun provideCartItemDao(productManagerDatabase: ProductManagerDatabase): CartItemDao{
        return productManagerDatabase.cartItemDao()
    }

    // For dataStore (user settings)
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }


    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository{
        return settingsRepositoryImpl
    }
}
