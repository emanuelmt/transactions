package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository

class SimpleTransactionAuthorizationUseCase(
    transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
): TransactionAuthorizationUseCaseBase(transactionRepository, walletRepository) {

    override suspend fun determineWallet(input: TransactionAuthorizationInput): WalletEntity? {
        val balanceType = merchantTypeToBalanceType(input.merchantType)
        return walletRepository.find(input.accountId, balanceType)
    }

}