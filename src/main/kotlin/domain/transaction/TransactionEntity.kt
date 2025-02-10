package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.BalanceType

data class TransactionEntity(val id: String, val accountId: String, val merchantTransactionType: String, val merchantName: String, val amount: Int)

fun getTransactionBalanceType(transaction: TransactionEntity): BalanceType{
    return when (transaction.merchantTransactionType) {
        "5411", "5412" -> BalanceType.FOOD
        "5811", "5812" -> BalanceType.MEAL
        else -> BalanceType.CASH
    }
}