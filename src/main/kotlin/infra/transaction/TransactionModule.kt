package dev.emanuelmt.infra.transaction

import dev.emanuelmt.domain.transaction.FallbackTransactionAuthorizationUseCase
import dev.emanuelmt.domain.transaction.MerchantTransactionAuthorizationUseCase
import dev.emanuelmt.domain.transaction.SimpleTransactionAuthorizationUseCase
import dev.emanuelmt.domain.transaction.TransactionAuthorizationInput
import dev.emanuelmt.infra.application.RequestLockerKey
import dev.emanuelmt.infra.application.getRepository
import dev.emanuelmt.infra.wallet.DatabaseWalletRepository
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTransactionModule() {
    val requestLocker = this.attributes[RequestLockerKey]
    val transactionRepository = this.getRepository<DatabaseTransactionRepository>()
    val walletRepository = this.getRepository<DatabaseWalletRepository>()
    val simpleTransactionAuthorizationUseCase =
        SimpleTransactionAuthorizationUseCase(requestLocker, transactionRepository, walletRepository)
    val fallbackTransactionAuthorizationUseCase =
        FallbackTransactionAuthorizationUseCase(requestLocker, transactionRepository, walletRepository)
    val merchantTransactionAuthorizationUseCase =
        MerchantTransactionAuthorizationUseCase(requestLocker, transactionRepository, walletRepository)

    routing {
        route("/transaction") {
            post("/simple-authorization") {
                val input = call.receive<TransactionAuthorizationInput>()
                val output = simpleTransactionAuthorizationUseCase.execute(input)

                call.respond(output)
            }
            post("/fallback-authorization") {
                val input = call.receive<TransactionAuthorizationInput>()
                val output = fallbackTransactionAuthorizationUseCase.execute(input)

                call.respond(output)
            }
            post("/merchant-authorization") {
                val input = call.receive<TransactionAuthorizationInput>()
                val output = merchantTransactionAuthorizationUseCase.execute(input)

                call.respond(output)
            }
        }
    }
}