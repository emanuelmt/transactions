package dev.emanuelmt.domain.wallet

interface WalletRepository {
    suspend fun save(wallet: WalletEntity)
    suspend fun update(wallet: WalletEntity)
    suspend fun saveBatch(wallets: List<WalletEntity>)
    suspend fun show(accountId: String): List<WalletEntity>
    suspend fun find(accountId: String, type: BalanceType): WalletEntity?
}