package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.account.AccountCreatedEvent
import dev.emanuelmt.domain.wallet.*

class AccountCreatedEventHandler(
    private val createInitialWalletUseCase: CreateInitialWalletUseCase
) {
    suspend fun handle(event: AccountCreatedEvent) {
        val input = CreateInitialWalletInput(event.account.id)
        createInitialWalletUseCase.execute(input)
    }
}