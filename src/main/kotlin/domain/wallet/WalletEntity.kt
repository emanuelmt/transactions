package dev.emanuelmt.domain.wallet

import java.util.UUID

data class WalletEntity(val id: String, val balance: Int, val type: BalanceType, val accountId: String)

fun WalletEntity.addBalance(balance: Int): WalletEntity {
    return this.copy(balance = this.balance + balance)
}

fun newWallet(balanceType: BalanceType, accountId: String): WalletEntity{
    return WalletEntity(UUID.randomUUID().toString(), 0, balanceType, accountId)
}