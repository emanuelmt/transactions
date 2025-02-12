package dev.emanuelmt

import dev.emanuelmt.infra.account.configureAccountModule
import dev.emanuelmt.infra.transaction.configureTransactionModule
import dev.emanuelmt.infra.wallet.configureWalletModule
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureAccountModule()
    configureWalletModule()
    configureTransactionModule()
}
