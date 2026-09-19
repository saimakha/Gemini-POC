package com.newcompose.geminipoc.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.newcompose.geminipoc.data.model.Product

@Preview(showBackground = true, widthDp = 400)
@Composable
fun ProductCardPreview() {
    MaterialTheme {
        Surface {
            ProductCard(
                product = Product(
                    id = 1,
                    name = "Premium Wireless Headphones",
                    category = "Electronics",
                    price = 99.99,
                    stock = mapOf(
                        "New York" to 15,
                        "Los Angeles" to 8,
                        "Chicago" to 0
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListViewPreview() {
    MaterialTheme {
        Surface {
            ProductListView(
                products = listOf(
                    Product(
                        id = 1,
                        name = "Premium Wireless Headphones",
                        category = "Electronics",
                        price = 99.99,
                        stock = mapOf("New York" to 15, "Los Angeles" to 8)
                    ),
                    Product(
                        id = 2,
                        name = "Smartphone Stand",
                        category = "Accessories",
                        price = 19.99,
                        stock = mapOf("New York" to 50, "Chicago" to 25)
                    ),
                    Product(
                        id = 3,
                        name = "USB-C Cable",
                        category = "Accessories",
                        price = 9.99,
                        stock = mapOf("New York" to 0, "Los Angeles" to 5)
                    )
                )
            )
        }
    }
}

