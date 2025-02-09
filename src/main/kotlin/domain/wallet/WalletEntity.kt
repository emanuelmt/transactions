package dev.emanuelmt.domain.wallet

data class WalletEntity(val id: String, val balance: Int, val type: BalanceType, val accountId: String)

