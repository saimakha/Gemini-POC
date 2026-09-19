package com.newcompose.geminipoc.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newcompose.geminipoc.data.model.Product

@Composable
fun ProductListView(
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductCard(product = product)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

