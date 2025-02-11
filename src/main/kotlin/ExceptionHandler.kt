package dev.emanuelmt

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
data class ValidationErrorResponse(val message: String, val reasons: List<String>)

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureExceptionHandler() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ValidationErrorResponse("some input params are invalid", cause.reasons),
            )
        }
        exception<BadRequestException> { call, cause ->
            when (cause.cause) {
                is MissingFieldException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "required input param ${cause.message} not passed"))
                }
                is JsonConvertException, is SerializationException -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "invalid input: ${cause.message}"))
                }
                else -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "invalid request: ${cause.message}"))
                }
            }
        }
   }
}