package dev.emanuelmt.infra.account

import dev.emanuelmt.domain.account.CreateAccountInput
import dev.emanuelmt.domain.account.CreateAccountUseCase
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAccountModule(){
    val accountRepository = InMemoryAccountRepository()
    val createAccountUseCase = CreateAccountUseCase(accountRepository)

    install(RequestValidation) {
        validate<CreateAccountInput> { input: CreateAccountInput ->
            if (input.name.length < 5)
                ValidationResult.Invalid("account name should have more than 5 characters")
            else ValidationResult.Valid
        }
    }

    routing {
        route("/account") {
            post("") {
                val input = call.receive<CreateAccountInput>()
                val output = createAccountUseCase.execute(input)

                call.respond(output)
            }
        }
    }
}