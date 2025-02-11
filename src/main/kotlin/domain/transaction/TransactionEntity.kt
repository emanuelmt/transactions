package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.BalanceType
import java.util.*

data class TransactionEntity(
    val id: String,
    val walletId: String,
    val merchantType: String,
    val merchantName: String,
    val amount: Int
)

fun newTransaction(
    walletId: String,
    merchantType: String,
    merchantName: String,
    amount: Int,
): TransactionEntity {
    return TransactionEntity(UUID.randomUUID().toString(), walletId, merchantType, merchantName, amount)
}

fun getTransactionBalanceType(transaction: TransactionEntity): BalanceType {
    return merchantTypeToBalanceType(transaction.merchantType)
}

fun merchantTypeToBalanceType(merchantType: String): BalanceType {
    return when (merchantType) {
        "5411", "5412" -> BalanceType.FOOD
        "5811", "5812" -> BalanceType.MEAL
        else -> BalanceType.CASH
    }
}