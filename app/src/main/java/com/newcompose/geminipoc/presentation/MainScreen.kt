package com.newcompose.geminipoc.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newcompose.geminipoc.presentation.components.ProductCard
import com.newcompose.geminipoc.presentation.components.ProductListView

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {

    val state by viewModel
        .state
        .collectAsState()

    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            placeholder = { Text("Search (e.g. electronics under 100)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = { viewModel.processQuery(query) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Ask Gemini",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Ask Gemini")
        }

        Spacer(Modifier.height(16.dp))

        if(state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Display products in card format
        if(state.displayedProducts.isNotEmpty()) {
            Text(
                text = "Search Results (${state.displayedProducts.size} found)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )
            ProductListView(products = state.displayedProducts)
        }
        // Display text result if no products
        else if(state.result.isNotEmpty()) {
            Text(
                text = state.result,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            )
        }
    }
}