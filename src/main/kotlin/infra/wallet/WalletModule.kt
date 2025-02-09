package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.wallet.CreateWalletInput
import dev.emanuelmt.domain.wallet.CreateWalletUseCase
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureWalletModule() {
    val walletRepository = InMemoryWalletRepository()
    val createWalletUseCase = CreateWalletUseCase(walletRepository)

    install(RequestValidation) {
        validate<CreateWalletInput> { input: CreateWalletInput ->
            if (input.initialBalance < 0)
                ValidationResult.Invalid("initial balance should be greater than or equals 0")
            else ValidationResult.Valid
        }
    }

    routing {
        route("/wallet") {
            post("") {
                val input = call.receive<CreateWalletInput>()
                val output = createWalletUseCase.execute(input)

                call.respond(output)
            }
        }
    }
}