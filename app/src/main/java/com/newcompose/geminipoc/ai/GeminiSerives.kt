package com.newcompose.geminipoc.ai

import com.google.ai.client.generativeai.GenerativeModel

class GeminiService {

    private val model = GenerativeModel(
        modelName = "gemini-3.5-flash",
        apiKey = "AQ.Ab8RN6Kp4K5cBFk4-lEovgbHJ5U0aFxoFrXUV-4tIDCQd9nGfQ"
    )

    suspend fun understandQuery(
        query: String
    ): String {

        // Instruct the model to return structured JSON. For SEARCH actions return an "items" array
        // where each item is a product object matching our Product model.
        // Example response for a search should look like:
        // {
        //   "action":"SEARCH",
        //   "items": [
        //     { "id": 1, "name": "Phone X", "category": "Mobile", "price": 499.99, "stock": { "NY": 10, "LA": 5 } },
        //     { "id": 2, "name": "Phone Y", "category": "Mobile", "price": 299.99, "stock": { "NY": 0, "LA": 3 } }
        //   ]
        // }
        val prompt = """
Return ONLY valid JSON.

If the user query indicates a search, return a JSON object with an "action":"SEARCH" and an "items" array.
Each item must be an object with fields: id (int), name (string), category (string), price (number), stock (object mapping location->quantity).

Example SEARCH response:
{
  "action":"SEARCH",
  "items":[
    {"id":1,"name":"Phone X","category":"Mobile","price":499.99,"stock":{"New York":10,"Los Angeles":5},"imageUrl":"https://example.com/phone_x.jpg","features":["Bluetooth","Noise Cancelling"],"tags":["bestseller","new"]},
    {"id":2,"name":"Phone Y","category":"Mobile","price":299.99,"stock":{"New York":0,"Los Angeles":3},"imageUrl":"https://example.com/phone_y.jpg","features":["Fast Charging"],"tags":["budget"]}
  ]
}

If the user asks to CHECK_STOCK return:
{
  "action":"CHECK_STOCK",
  "product":"iPhone 15",
  "store":"Mumbai"
}

User Query:
$query

Do not return explanations, markdown, or extra text. Return JSON only.
""".trimIndent()
        val response =
            model.generateContent(prompt)

        return response.text ?: ""
    }
}