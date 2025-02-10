package dev.emanuelmt

import dev.emanuelmt.infra.account.accountValidation
import dev.emanuelmt.infra.wallet.walletValidation
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation){
        accountValidation()
        walletValidation()
    }
}
