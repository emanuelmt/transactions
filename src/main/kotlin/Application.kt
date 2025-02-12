package dev.emanuelmt

import dev.emanuelmt.infra.application.configureDatabases
import dev.emanuelmt.infra.application.configureDomainEvents
import dev.emanuelmt.infra.application.configureRepositories
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureValidation()
    configureDatabases()
    configureRepositories()
    configureExceptionHandler()
    configureDomainEvents()
    configureRouting()
}
