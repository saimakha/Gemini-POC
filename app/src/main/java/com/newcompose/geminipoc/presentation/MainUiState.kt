package com.newcompose.geminipoc.presentation

import com.newcompose.geminipoc.data.model.Product

data class MainUiState(

    val userInput: String = "",

    val result: String = "",

    val displayedProducts: List<Product> = emptyList(),

    val loading: Boolean = false
)
