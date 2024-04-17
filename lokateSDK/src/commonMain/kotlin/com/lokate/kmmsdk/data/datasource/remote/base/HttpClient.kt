package com.lokate.kmmsdk.data.datasource.remote.base

import com.lokate.kmmsdk.data.datasource.remote.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerializationException

@Suppress("TooGenericExceptionCaught", "MaxLineLength")
suspend inline fun <reified T, reified E> HttpClient.lokateRequest(block: HttpRequestBuilder.() -> Unit): ApiResponse<T, E> =
    try {
        val response = request(block)
        ApiResponse.Success(response.body())
    } catch (exception: ClientRequestException) {
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = exception.response.body(),
            errorMessage = exception.response.status.description,
        )
    } catch (exception: HttpExceptions) {
        ApiResponse.Error.HttpError(
            code = exception.response.status.value,
            errorBody = exception.response.body(),
            errorMessage = exception.response.status.description,
        )
    } catch (exception: SerializationException) {
        ApiResponse.Error.SerializationError(exception.message, errorMessage = "Serialization Error")
    } catch (exception: Exception) {
        ApiResponse.Error.GenericError(exception.message, null)
    }

class HttpExceptions(
    response: HttpResponse,
    failureReason: String?,
    cachedResponseText: String,
) : ResponseException(response, cachedResponseText) {
    override val message: String = "Status: ${response.status}." + " Failure: $failureReason"
}
