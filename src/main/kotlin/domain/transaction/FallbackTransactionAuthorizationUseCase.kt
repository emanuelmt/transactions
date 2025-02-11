package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.*

class FallbackTransactionAuthorizationUseCase(
    transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
): TransactionAuthorizationUseCaseBase(transactionRepository, walletRepository) {

    override suspend fun determineWallet(
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