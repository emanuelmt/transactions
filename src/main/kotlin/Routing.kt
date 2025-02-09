package dev.emanuelmt

import dev.emanuelmt.infra.account.configureAccountModule
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureAccountModule()
}
