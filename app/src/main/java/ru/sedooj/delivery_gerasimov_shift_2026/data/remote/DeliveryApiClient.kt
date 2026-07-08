package ru.sedooj.delivery_gerasimov_shift_2026.data.remote

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class DeliveryApiClient(
    baseUrl: String
) {
    private val normalizedBaseUrl = baseUrl.trimEnd('/')

    fun get(path: String): JSONObject {
        return request(path = path, method = HttpMethod.Get)
    }

    fun post(path: String, body: JSONObject): JSONObject {
        return request(path = path, method = HttpMethod.Post, body = body)
    }

    private fun request(
        path: String,
        method: HttpMethod,
        body: JSONObject? = null
    ): JSONObject {
        val connection = (URL("$normalizedBaseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method.value
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty(HEADER_ACCEPT, CONTENT_TYPE_JSON)
            setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
        }

        try {
            if (body != null) {
                connection.doOutput = true
                connection.outputStream.use { output ->
                    output.write(body.toString().toByteArray(Charsets.UTF_8))
                }
            }

            val responseBody = if (connection.responseCode in HTTP_SUCCESS_RANGE) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                    .ifBlank { connection.responseMessage.orEmpty() }
                    .also { message ->
                        throw IOException("Delivery API ${connection.responseCode}: $message")
                    }
            }

            return JSONObject(responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private enum class HttpMethod(val value: String) {
        Get("GET"),
        Post("POST")
    }

    private companion object {
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 15_000
        const val HEADER_ACCEPT = "Accept"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        val HTTP_SUCCESS_RANGE = 200..299
    }
}
