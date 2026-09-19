package com.newcompose.geminipoc.utils


import org.json.JSONObject

object GeminiResponseParser {

    fun parse(json:String): JSONObject {

        return JSONObject(json)
    }
}
