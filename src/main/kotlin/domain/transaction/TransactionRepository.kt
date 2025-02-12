package dev.emanuelmt.domain.transaction

interface TransactionRepository {
    suspend fun save(transaction: TransactionEntity, updateWalletCall: suspend ()-> Unit)
}