package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.*

class FallbackTransactionAuthorizationUseCase(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    suspend fun execute(input: TransactionAuthorizationInput): TransactionAuthorizationOutput {
        try {
            determineWallet(input)?.let { walletToUse ->
                if (!walletToUse.hasSufficientBalance(input.amount)) return TransactionAuthorizationOutput(
                    TransactionStatusCode.InsufficientBalance
                )
                walletToUse.subBalance(input.amount).also {
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

    private suspend fun determineWallet(
        input: TransactionAuthorizationInput
    ): WalletEntity? {
        val balanceType = merchantTypeToBalanceType(input.merchantType)
        val primaryWallet = walletRepository.find(input.accountId, balanceType)
        val fallbackWallet =
            if (!isFallbackBalanceType(balanceType)) walletRepository.find(
                input.accountId,
                FallbackBalanceTye
            ) else null

        return when {
            primaryWallet != null && primaryWallet.hasSufficientBalance(input.amount) -> primaryWallet
            fallbackWallet != null -> fallbackWallet
            else -> null
        }
    }

}