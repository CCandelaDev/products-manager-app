package com.ccandeladev.androidtesting.productlist.data.remote

import com.ccandeladev.androidtesting.core.domain.model.AppError
import com.ccandeladev.androidtesting.productlist.data.remote.response.InventoryResponse
import com.ccandeladev.androidtesting.productlist.data.remote.response.ProductResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RemoteDataSourceTest {

    private val server = MockWebServer()
    private lateinit var json: Json
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        server.start()
        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory(contentType = "application/json".toMediaType()))
            .build()

        val api = retrofit.create(ProductManagerApiService::class.java)
        remoteDataSource = RemoteDataSource(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    //valid for short answers
    @Test
    fun `given empty json response when getProducts then returns empty list `() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"products":[]}""") //Raw string
        )
        val result = remoteDataSource.getInventory()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    //Valid for fixed answers
    @Test
    fun `given valid json response when getProducts then returns mapped dto`() = runTest {
        val jsonResource = ClassLoader.getSystemResource("products_success.json").readText()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResource)
        )

        val result = remoteDataSource.getInventory()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isNotEmpty())
    }

    @Test
    fun `given serialize project when getProducts then data matches original object`() = runTest {
        val productResponse = ProductResponse(
            id = "id1",
            name = "shoes",
            priceCents = 100,
            category = "Footwear",
            stock = 5
        )

        val jsonString = json.encodeToString(InventoryResponse(listOf(productResponse)))
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonString)
        )

        val result = remoteDataSource.getInventory()

        assertTrue(result.isSuccess)
        val products = result.getOrThrow()
        assertTrue(products.size == 1)
        assertTrue(products[0].id == productResponse.id)
        assertTrue(products[0].name == productResponse.name)
        assertTrue(products[0].category == productResponse.category)
    }

    @Test
    fun `given 404 response when getProducts then returns NotFoundError`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))

        val result = remoteDataSource.getInventory()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.NotFoundError)
    }

    @Test
    fun `given  malformed json when getProducts then returns UnKnownError`() = runTest {
        server.enqueue(MockResponse().setBody("errordawdad").setResponseCode(200))

        val result = remoteDataSource.getInventory()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.UnknownError)
    }

    @Test
    fun `given  offers request when getProducts then calls correct endpoint`() = runTest {
        server.enqueue(MockResponse().setBody("""{"offers":[]}""").setResponseCode(200))

        remoteDataSource.getInventory()
        val result = server.takeRequest()

        val request = server.takeRequest()
        assertEquals("/data/v1/offers.json", result.path)
        assertEquals("GET", result.method)
    }
}
