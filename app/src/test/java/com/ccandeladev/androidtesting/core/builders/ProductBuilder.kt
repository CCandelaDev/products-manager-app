package com.ccandeladev.androidtesting.core.builders

import com.ccandeladev.androidtesting.productlist.domain.model.Product

const val DEFAULT_PRODUCT_ID = "product-id"
class ProductBuilder {

    //Product attributes by default
    //private var id: String = "product-1"
    private var id: String = DEFAULT_PRODUCT_ID
    private var name: String = "product example"
    private var description: String = "Complete description"
    private var price: Double = 10.0
    private var category: String = "Test category"
    private var stock: Int = 10
    private var imageUrl: String? = null


    fun withId(id: String) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withDescription(description: String) = apply { this.description = description }
    fun withPrice(price: Double) = apply { this.price = price }
    fun withCategory(category: String) = apply { this.category = category }
    fun withStock(stock: Int) = apply { this.stock = stock }
    fun withImageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }


    fun build(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            stock = stock,
            imageUrl = imageUrl
        )
    }
}

// I can to pass only the products that I want to test, and the rest will be default
fun product(block: ProductBuilder.() -> Unit = {}) = ProductBuilder().apply(block).build()