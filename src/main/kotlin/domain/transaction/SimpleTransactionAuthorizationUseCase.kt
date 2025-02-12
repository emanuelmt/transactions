package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.application.RequestLocker
import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository

class SimpleTransactionAuthorizationUseCase(
    requestLocker: RequestLocker,
    transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
) : TransactionAuthorizationUseCaseBase(requestLocker, transactionRepository, walletRepository) {

    override suspend fun determineWallet(input: TransactionAuthorizationInput): WalletEntity? {
        val balanceType = merchantTypeToBalanceType(input.merchantType)
        return walletRepository.find(input.accountId, balanceType)
    }

}