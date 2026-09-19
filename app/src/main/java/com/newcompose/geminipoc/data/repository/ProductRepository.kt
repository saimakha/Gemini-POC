package com.newcompose.geminipoc.data.repository

import com.newcompose.geminipoc.data.model.*


class ProductRepository {

    private val products = listOf(

        Product(
            1,
            "iPhone 15",
            "Mobile",
            80000.0,
            mapOf(
                "Mumbai" to 10,
                "Pune" to 5
            )
        ),

        Product(
            2,
            "Samsung TV",
            "Electronics",
            50000.0,
            mapOf(
                "Mumbai" to 2,
                "Pune" to 0
            )
        )
    )

    private val cart = mutableListOf<CartItem>()

    fun getProducts() = products

    fun getCart() = cart

    fun addToCart(productName: String, qty: Int) {

        val product =
            products.find { it.name.equals(productName, true) }
                ?: return

        cart.add(
            CartItem(product, qty)
        )
    }

    fun removeFromCart(productName: String) {

        cart.removeIf {
            it.product.name.equals(productName, true)
        }
    }
}