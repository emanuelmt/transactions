package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository

class MerchantTransactionAuthorizationUseCase(
    transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
) : TransactionAuthorizationUseCaseBase(transactionRepository, walletRepository) {

    override suspend fun determineWallet(
        input: TransactionAuthorizationInput
    ): WalletEntity? {
        val balanceType = merchantNameToBalanceType(input.merchantName, input.merchantType)
        return walletRepository.find(input.accountId, balanceType)
    }

}