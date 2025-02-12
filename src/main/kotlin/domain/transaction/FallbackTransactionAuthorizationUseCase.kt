package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.application.RequestLocker
import dev.emanuelmt.domain.wallet.*

class FallbackTransactionAuthorizationUseCase(
    requestLocker: RequestLocker,
    transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
) : TransactionAuthorizationUseCaseBase(requestLocker, transactionRepository, walletRepository) {

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