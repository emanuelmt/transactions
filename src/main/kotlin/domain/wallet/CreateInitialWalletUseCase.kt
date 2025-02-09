package dev.emanuelmt.domain.wallet

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateInitialWalletInput(val accountId: String)

@Serializable
data class CreatedWallet(val id: String, val balance: Int, val type: BalanceType)
data class CreateInitialWalletOutput(val wallets: List<CreatedWallet>)


class CreateInitialWalletUseCase(private val repository: WalletRepository) {
    suspend fun execute(input: CreateInitialWalletInput): CreateInitialWalletOutput {
        val wallets = mutableListOf<WalletEntity>()

        for (balanceType in BalanceType.entries){
            val wallet = WalletEntity(UUID.randomUUID().toString(), 0, balanceType, input.accountId)
            wallets.add(wallet)
        }

        this.repository.saveBatch(wallets)

        return CreateInitialWalletOutput(wallets.map{ wallet -> CreatedWallet(wallet.id, wallet.balance, wallet.type) })
    }
}