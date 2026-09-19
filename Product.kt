package com.newcompose.geminipoc.data.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Map<String, Int>,
    // Optional image URL for product thumbnail
    val imageUrl: String? = null,
    // Additional lists the user requested
    val features: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)
