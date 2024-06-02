package com.example.mymaptestapp.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

class JSONSerializer {
    companion object {
        fun mapToJson(map: Map<String, Any>): String {
            val jsonObject = JSONObject()

            for ((key, value) in map) {
                jsonObject.put(key, value)
            }

            return jsonObject.toString()
        }

        fun jsonToMap(json: String): Map<String, Any> {
            val jsonObject = JSONObject(json)

            val map = mutableMapOf<String, Any>()
            val iterator = jsonObject.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = jsonObject.get(key)
            }

            return map
        }

        fun responseBodyToMap(responseBody: ResponseBody): Map<String, Any> {
            return jsonToMap(responseBody.string())
        }

        fun mapToRequestBody(map: Map<String, Any>): RequestBody {
            val json = mapToJson(map)
            return json.toRequestBody("application/json".toMediaType())
        }
    }
}