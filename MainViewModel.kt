package com.newcompose.geminipoc.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newcompose.geminipoc.ai.GeminiService
import com.newcompose.geminipoc.data.repository.ProductRepository
import com.newcompose.geminipoc.domain.ShoppingUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val repository =
        ProductRepository()

    private val useCases =
        ShoppingUseCases(repository)

    private val gemini =
        GeminiService()

    private val _state =
        MutableStateFlow(MainUiState())

    val state =
        _state.asStateFlow()

    // Attempt to extract and normalize JSON embedded in model responses
    private fun sanitizeResponse(text: String): String {
        var s = text.trim()

        // Remove common markdown fences and language hints like ```json
        s = s.replace("```json", "", ignoreCase = true)
        s = s.replace("```", "")
        s = s.replace("`json", "", ignoreCase = true)
        s = s.replace("`", "")

        // Find the first { and last } - extract the substring likely containing JSON
        val start = s.indexOf('{')
        val end = s.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            s = s.substring(start, end + 1)
        }

        // Normalize smart quotes to straight quotes
        s = s.replace("“", "\"").replace("”", "\"")

        // If only single quotes are used for strings, replace them with double quotes
        // First, a safe global replacement for lone single-quoted strings
        if (s.contains("'") && !s.contains('"')) {
            s = s.replace("'", "\"")
        } else {
            // Replace single-quoted string occurrences 'text' -> "text"
            val regex = Regex("'([^']*)'")
            s = regex.replace(s) { m ->
                val inner = m.groupValues[1]
                // Escape any existing double quotes inside
                val escaped = inner.replace("\"", "\\\"")
                "\"$escaped\""
            }
        }

        // Remove trailing commas before closing braces/brackets which are invalid in strict JSON
        try {
            val trailingCommaRegex = Regex(",\\s*([}\\]])")
            s = trailingCommaRegex.replace(s) { m -> m.groupValues[1] }
        } catch (_: Exception) {
        }

        return s
    }

    fun processQuery(query: String) {

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    loading = true
                )

            try {

                val response =
                    gemini.understandQuery(query)

                try {

                    // Try to sanitize the response to valid JSON when models return markdown or single quotes
                    val cleaned = sanitizeResponse(response)
                    val json = JSONObject(cleaned)

                    val action = json.optString("action", "")

                    if (action == "SEARCH") {
                        // The model can return an "items" array with product objects.
                        val items = json.optJSONArray("items")
                        if (items != null) {
                            val productList = mutableListOf<com.newcompose.geminipoc.data.model.Product>()
                            for (i in 0 until items.length()) {
                                try {
                                    val obj = items.getJSONObject(i)
                                    val id = obj.optInt("id", i + 1)
                                    val name = obj.optString("name", "")
                                    val category = obj.optString("category", "")
                                    val price = obj.optDouble("price", 0.0)
                                    val stockObj = obj.optJSONObject("stock")
                                    val stockMap = mutableMapOf<String, Int>()
                                    // optional extra fields
                                    val imageUrl = obj.optString("imageUrl", null)
                                    val features = mutableListOf<String>()
                                    val tags = mutableListOf<String>()
                                    if (stockObj != null) {
                                        val keys = stockObj.keys()
                                        while (keys.hasNext()) {
                                            val key = keys.next()
                                            val quantity = stockObj.optInt(key, 0)
                                            stockMap[key] = quantity
                                        }
                                    }
                                    // parse features array
                                    val featuresArr = obj.optJSONArray("features")
                                    if (featuresArr != null) {
                                        for (fi in 0 until featuresArr.length()) {
                                            val f = featuresArr.optString(fi, null)
                                            if (f != null) features.add(f)
                                        }
                                    }
                                    val tagsArr = obj.optJSONArray("tags")
                                    if (tagsArr != null) {
                                        for (ti in 0 until tagsArr.length()) {
                                            val t = tagsArr.optString(ti, null)
                                            if (t != null) tags.add(t)
                                        }
                                    }

                                    productList.add(
                                        com.newcompose.geminipoc.data.model.Product(
                                            id = id,
                                            name = name,
                                            category = category,
                                            price = price,
                                            stock = stockMap,
                                            imageUrl = if (imageUrl.isNullOrBlank()) null else imageUrl,
                                            features = features,
                                            tags = tags
                                        )
                                    )
                                } catch (_: Exception) {
                                    // skip malformed item
                                }
                            }

                            _state.value = MainUiState(
                                result = "Found ${productList.size} items",
                                displayedProducts = productList,
                                loading = false
                            )
                        } else {
                            // fallback: if no items array, try to interpret as filters and use local search
                            val category = json.optString("category", "")
                            val maxPrice = json.optDouble("maxPrice", Double.MAX_VALUE)
                            val products = useCases.searchProducts(category, maxPrice)
                            val resultString = useCases.search(category, maxPrice)
                            _state.value = MainUiState(
                                result = resultString,
                                displayedProducts = products,
                                loading = false
                            )
                        }
                    } else {
                        // Non-search actions: preserve existing behavior
                        _state.value = MainUiState(
                            result = response,
                            displayedProducts = emptyList(),
                            loading = false
                        )
                    }

                } catch (_: Exception) {
                    // Try one more time to salvage JSON by extracting an items array if present
                    try {
                        val cleanedAgain = sanitizeResponse(response)
                        fun extractItemsArray(s: String): String? {
                            val key = "\"items\""
                            val idx = s.indexOf(key)
                            if (idx == -1) return null
                            val arrStart = s.indexOf('[', idx)
                            if (arrStart == -1) return null
                            var depth = 0
                            for (i in arrStart until s.length) {
                                when (s[i]) {
                                    '[' -> depth++
                                    ']' -> {
                                        depth--
                                        if (depth == 0) return s.substring(arrStart, i + 1)
                                    }
                                }
                            }
                            return null
                        }

                        val itemsJson = extractItemsArray(cleanedAgain)
                        if (itemsJson != null) {
                            val wrapper = "{\"action\":\"SEARCH\",\"items\":$itemsJson}"
                            val json2 = JSONObject(wrapper)
                            val items2 = json2.optJSONArray("items")
                            val productList = mutableListOf<com.newcompose.geminipoc.data.model.Product>()
                            if (items2 != null) {
                                for (i in 0 until items2.length()) {
                                    try {
                                        val obj = items2.getJSONObject(i)
                                        val id = obj.optInt("id", i + 1)
                                        val name = obj.optString("name", "")
                                        val category = obj.optString("category", "")
                                        val price = obj.optDouble("price", 0.0)
                                        val stockObj = obj.optJSONObject("stock")
                                        val stockMap = mutableMapOf<String, Int>()
                                        if (stockObj != null) {
                                            val keys = stockObj.keys()
                                            while (keys.hasNext()) {
                                                val key = keys.next()
                                                val quantity = stockObj.optInt(key, 0)
                                                stockMap[key] = quantity
                                            }
                                        }
                                        val imageUrl = obj.optString("imageUrl", null)
                                        val features = mutableListOf<String>()
                                        val featuresArr = obj.optJSONArray("features")
                                        if (featuresArr != null) {
                                            for (fi in 0 until featuresArr.length()) {
                                                val f = featuresArr.optString(fi, null)
                                                if (f != null) features.add(f)
                                            }
                                        }
                                        val tags = mutableListOf<String>()
                                        val tagsArr = obj.optJSONArray("tags")
                                        if (tagsArr != null) {
                                            for (ti in 0 until tagsArr.length()) {
                                                val t = tagsArr.optString(ti, null)
                                                if (t != null) tags.add(t)
                                            }
                                        }
                                        productList.add(
                                            com.newcompose.geminipoc.data.model.Product(
                                                id = id,
                                                name = name,
                                                category = category,
                                                price = price,
                                                stock = stockMap,
                                                imageUrl = if (imageUrl.isNullOrBlank()) null else imageUrl,
                                                features = features,
                                                tags = tags
                                            )
                                        )
                                    } catch (_: Exception) {
                                    }
                                }
                            }

                            _state.value = MainUiState(
                                result = "Found ${productList.size} items",
                                displayedProducts = productList,
                                loading = false
                            )
                        } else {
                            _state.value = MainUiState(
                                result = "Unexpected Response:\n$cleanedAgain",
                                loading = false
                            )
                        }
                    } catch (_: Exception) {
                        _state.value = MainUiState(
                            result = "Unexpected Response:\n$response",
                            loading = false
                        )
                    }
                }

            } catch (e: Exception) {

                _state.value =
                    MainUiState(
                        result = e.message ?: "Unknown Error",
                        loading = false
                    )
            }
        }
    }
}