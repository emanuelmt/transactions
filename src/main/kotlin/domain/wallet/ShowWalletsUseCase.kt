package dev.emanuelmt.domain.wallet

import io.ktor.server.plugins.*
import kotlinx.serialization.Serializable

@Serializable
data class ShowWalletsInput(val accountId: String)

@Serializable
data class WalletData(val id: String, val balance: Int, val type: BalanceType)

@Serializable
data class ShowWalletsOutput(val wallets: List<WalletData>)

class ShowWalletsUseCase(private val repository: WalletRepository) {
    suspend fun execute(input: ShowWalletsInput): ShowWalletsOutput {
        val wallets = repository.show(input.accountId)

        if (wallets.isEmpty()) {
            throw NotFoundException()
        }

        return ShowWalletsOutput(wallets.map { wallet -> WalletData(wallet.id, wallet.balance, wallet.type) })
    }
}