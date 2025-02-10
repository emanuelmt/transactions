package dev.emanuelmt.domain.wallet

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddWalletBalanceInput(val accountId: String, val balance: Int, val type: BalanceType)

@Serializable
data class AddWalletBalanceOutput(val id: String, val balance: Int)

class AddWalletBalanceUseCase(private val repository: WalletRepository) {
    suspend fun execute(input: AddWalletBalanceInput): AddWalletBalanceOutput {
        val updatedWallet = repository.find(input.accountId, input.type)?.let { existingWallet ->
            existingWallet.addBalance(input.balance).also {
                repository.update(it)
            }
        } ?: newWallet(input.type, input.accountId).addBalance(input.balance).also {
            repository.save(it)
        }

        return AddWalletBalanceOutput(updatedWallet.id, updatedWallet.balance)
    }
}