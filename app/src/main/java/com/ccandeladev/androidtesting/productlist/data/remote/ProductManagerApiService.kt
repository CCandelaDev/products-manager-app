package com.ccandeladev.androidtesting.productlist.data.remote

import com.ccandeladev.androidtesting.productlist.data.remote.response.InventoryResponse
import com.ccandeladev.androidtesting.productlist.data.remote.response.OffersResponse
import retrofit2.http.GET

//Call to backend
interface ProductManagerApiService {
    @GET("data/v1/inventory.json")
    suspend fun getInventory(): InventoryResponse // response

    @GET("data/v1/offers.json")
    suspend fun getOffers(): OffersResponse  // response
}