package com.example.bricole.api

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor(
    val enabled: Boolean
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (enabled.not()) {
            return chain.proceed(chain.request())
        }
        val request = chain.request()
        val encodedPath = request.url.encodedPath
        if (encodedPath == "/services") {
            val body = servicesJson.toResponseBody()
            return getResponse(request, body)
        } else if (encodedPath.contains("/services/providers/")) {
            val body = providersJson.toResponseBody()
            return getResponse(request, body)
        }
        return chain.proceed(chain.request())
    }

    private fun getResponse(
        request: Request,
        body: ResponseBody
    ): Response {
        return Response.Builder()
            .code(200)
            .protocol(Protocol.HTTP_2)
            .message("This is just a mock data")
            .request(request)
            .body(body)
            .build()
    }
}


private const val servicesJson =
    "[\n  {\n    \"id\": 1,\n    \"name\": \"Plumbing\",\n    \"icon\": \"\\uD83D\\uDD27\"\n  },\n  {\n    \"id\": 2,\n    \"name\": \"Electrical\",\n    \"icon\": \"⚡\"\n  }\n]"

private const val providersJson =
    "[\n  {\n    \"proId\": 1,\n    \"proName\": \"Abdellatif\",\n    \"phone\": \"+212666666666\",\n    \"city\": \"Casablanca\",\n    \"serviceId\": 1,\n    \"serviceName\": \"Plumbing\"\n  },\n  {\n    \"proId\": 2,\n    \"proName\": \"Abdelhadi\",\n    \"phone\": \"+212666666666\",\n    \"city\": \"Zagora\",\n    \"serviceId\": 2,\n    \"serviceName\": \"Electrical\"\n  }\n]"