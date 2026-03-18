package com.ccandeladev.androidtesting.productlist.data.remote

import com.ccandeladev.androidtesting.core.core.AppError
import com.ccandeladev.androidtesting.productlist.data.remote.response.OfferResponse
import com.ccandeladev.androidtesting.productlist.data.remote.response.ProductResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

//Inject API service (ProductManagerApiService)
class RemoteDataSource @Inject constructor(val productManagerApiService: ProductManagerApiService) {

    suspend fun getInventory(): Result<List<ProductResponse>> {
        return try {
            val response = productManagerApiService.getInventory()
            Result.success(response.inventory)
        }catch (e: Exception) {
            Result.failure(mapToDomainError(e = e))
        }
    }

    suspend fun getOffers(): Result<List<OfferResponse>>{
        return try {
            val response = productManagerApiService.getOffers()
            Result.success(response.offers)
        }catch (e: Exception){
            Result.failure(mapToDomainError(e = e))
        }

    }

    // To define several errors with sealed class AppError
    private fun mapToDomainError(e: Exception): AppError {
        return when(e) {
            is UnknownHostException -> AppError.NetworkError
            is SocketTimeoutException -> AppError.NetworkError
            is IOException -> AppError.NetworkError
            is HttpException -> {
                when (e.code()) {
                    404 -> AppError.NotFoundError
                    else -> AppError.NetworkError
                }
            }
            else -> AppError.UnknownError(e.message)

        }
    }
}