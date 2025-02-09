package dev.emanuelmt.domain.wallet

interface WalletRepository {
    suspend fun save(wallet: WalletEntity)
    suspend fun saveBatch(wallets: List<WalletEntity>)
}