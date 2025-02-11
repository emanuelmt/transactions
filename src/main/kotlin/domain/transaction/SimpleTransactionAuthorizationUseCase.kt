package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.WalletRepository
import dev.emanuelmt.domain.wallet.subBalance

class SimpleTransactionAuthorizationUseCase(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    suspend fun execute(input: TransactionAuthorizationInput): TransactionAuthorizationOutput {
        val balanceType = merchantTypeToBalanceType(input.merchantType)

        try {
            walletRepository.find(input.accountId, balanceType)?.let { wallet ->
                if (wallet.balance < input.amount) return TransactionAuthorizationOutput(TransactionStatusCode.InsufficientBalance)
                wallet.subBalance(input.amount).also {
                    val transaction = newTransaction(it.id, input.merchantType, input.merchantName, input.amount)
                    transactionRepository.save(transaction) { walletRepository.update(it) }
                    return TransactionAuthorizationOutput(TransactionStatusCode.ApprovedTransaction)
                }
            }
        } catch (exception: Exception) {
            println(exception)
        }

        return TransactionAuthorizationOutput(TransactionStatusCode.RejectedTransaction)
    }
}