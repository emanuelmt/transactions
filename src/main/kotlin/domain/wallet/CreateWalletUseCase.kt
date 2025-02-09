package dev.emanuelmt.domain.wallet

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateWalletInput(val accountId: String, val initialBalance: Int, val type: BalanceType)

@Serializable
data class CreateWalletOutput(val id: String)

class CreateWalletUseCase(private val repository: WalletRepository) {
    suspend fun execute(input: CreateWalletInput): CreateWalletOutput {
        val wallet = WalletEntity(UUID.randomUUID().toString(), input.initialBalance, input.type, input.accountId)

        this.repository.save(wallet)

        return CreateWalletOutput(wallet.id)
    }
}