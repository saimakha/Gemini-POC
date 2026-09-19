package com.newcompose.geminipoc.domain


import com.newcompose.geminipoc.data.model.Product
import com.newcompose.geminipoc.data.repository.ProductRepository

class ShoppingUseCases(
    private val repository: ProductRepository
) {

    fun search(
        category: String,
        maxPrice: Double
    ): String {

        val products =
            repository.getProducts()
                .filter {

                    it.category.equals(
                        category,
                        true
                    )
                            &&
                            it.price <= maxPrice
                }

        return products.joinToString("\n") {
            "${it.name} ₹${it.price}"
        }
    }

    fun searchProducts(
        category: String,
        maxPrice: Double
    ): List<Product> {

        return repository.getProducts()
            .filter {
                it.category.equals(
                    category,
                    true
                )
                        &&
                        it.price <= maxPrice
            }
    }

    fun stock(
        productName:String,
        store:String
    ): String {

        val product =
            repository.getProducts()
                .find {
                    it.name.equals(productName, true)
                }

        val stock =
            product?.stock?.get(store)
                ?: 0

        return "$stock available"
    }
}