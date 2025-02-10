package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.account.AccountCreatedEvent
import dev.emanuelmt.domain.account.validateAccountId
import dev.emanuelmt.domain.wallet.*
import dev.emanuelmt.infra.application.DatabaseConnectionKey
import dev.emanuelmt.infra.application.DomainEventPublisherKey
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun RequestValidationConfig.walletValidation() {
    validate<CreateWalletInput> { input: CreateWalletInput ->
        if (input.initialBalance < 0)
            ValidationResult.Invalid("initial balance should be greater than or equals 0")
        else ValidationResult.Valid
    }
}

fun Application.configureWalletModule() {
    val walletRepository = InMemoryWalletRepository(this.attributes[DatabaseConnectionKey])
    val createWalletUseCase = CreateWalletUseCase(walletRepository)
    val createInitialWalletUseCase = CreateInitialWalletUseCase(walletRepository)
    val showWalletsUseCase = ShowWalletsUseCase(walletRepository)

    val accountCreatedHandler = AccountCreatedEventHandler(createInitialWalletUseCase)
    val domainEventPublisher = this.attributes[DomainEventPublisherKey]
    domainEventPublisher.subscribe(AccountCreatedEvent::class.java) { event ->
        accountCreatedHandler.handle(event)
    }

    routing {
        route("/wallet") {
            post("") {
                val input = call.receive<CreateWalletInput>()
                val output = createWalletUseCase.execute(input)

                call.respond(output)
            }
            get("/{accountId}") {
                val accountId = validateAccountId(call.parameters["accountId"])

                val output = showWalletsUseCase.execute(ShowWalletsInput(accountId))

                call.respond(output)
            }
        }
    }
}