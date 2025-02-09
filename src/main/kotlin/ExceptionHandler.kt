package dev.emanuelmt

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable

@Serializable
data class ValidationErrorResponse(val message: String, val reasons: List<String>)

fun Application.configureExceptionHandler() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(
                ValidationErrorResponse("some input params are invalid", cause.reasons),
                typeInfo<ValidationErrorResponse>()
            )
        }
   }
}