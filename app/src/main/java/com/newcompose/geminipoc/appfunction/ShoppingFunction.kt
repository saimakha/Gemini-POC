package com.newcompose.geminipoc.appfunction

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.newcompose.geminipoc.data.repository.ProductRepository

class ShoppingFunctions {

    private val repository = ProductRepository()

    @AppFunction
    suspend fun searchProducts(
        context: AppFunctionContext,
        category: String
    ): String {

        val products = repository.getProducts()
            .filter {
                it.category.equals(category, true)
            }

        return products.joinToString("\n") {
            "${it.name} - ₹${it.price}"
        }
    }

    @AppFunction
    suspend fun checkStock(
        context: AppFunctionContext,
        productName: String,
        store: String
    ): String {

        val product = repository.getProducts()
            .find {
                it.name.equals(productName, true)
            }

        val quantity =
            product?.stock?.get(store) ?: 0

        return "$quantity available"
    }

    @AppFunction
    suspend fun addToCart(
        context: AppFunctionContext,
        productName: String,
        quantity: Int
    ): String {

        repository.addToCart(
            productName,
            quantity
        )

        return "Added successfully"
    }

    @AppFunction
    suspend fun removeFromCart(
        context: AppFunctionContext,
        productName: String
    ): String {

        repository.removeFromCart(productName)

        return "Removed successfully"
    }
}