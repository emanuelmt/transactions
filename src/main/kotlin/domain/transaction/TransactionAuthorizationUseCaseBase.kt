package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository
import dev.emanuelmt.domain.wallet.hasSufficientBalance
import dev.emanuelmt.domain.wallet.subBalance

abstract class TransactionAuthorizationUseCaseBase(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    suspend fun execute(input: TransactionAuthorizationInput): TransactionAuthorizationOutput {
        try {
            determineWallet(input)?.let { wallet ->
                return this.processTransaction(input, wallet)
            }
        } catch (exception: Exception) {
            println(exception)
        }

        return TransactionAuthorizationOutput(TransactionStatusCode.RejectedTransaction)
    }

    private suspend fun processTransaction(
        input: TransactionAuthorizationInput,
        wallet: WalletEntity
    ): TransactionAuthorizationOutput {
        if (!wallet.hasSufficientBalance(input.amount)) {
            return TransactionAuthorizationOutput(TransactionStatusCode.InsufficientBalance)
        }

        wallet.subBalance(input.amount).also {
            val transaction = newTransaction(it.id, input.merchantType, input.merchantName, input.amount)
            transactionRepository.save(transaction) { walletRepository.update(it) }
            return TransactionAuthorizationOutput(TransactionStatusCode.ApprovedTransaction)
        }
    }

    protected abstract suspend fun determineWallet(input:TransactionAuthorizationInput): WalletEntity?
}