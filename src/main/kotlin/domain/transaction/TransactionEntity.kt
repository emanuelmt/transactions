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

fun merchantNameToBalanceType(merchantName: String, merchantType: String): BalanceType {
    val bestMatch = MerchantClassifierRules.mapNotNull { rule ->
        rule.regex.find(merchantName)?.let {
            val score = rule.weight
            rule to score
        }
    }.maxByOrNull { it.second }

    return bestMatch?.first?.category ?: merchantTypeToBalanceType(merchantType)
}

data class MerchantRule(val regex: Regex, val category: BalanceType, val weight: Int)

private val MerchantClassifierRules = listOf(
    MerchantRule("(?i).*uber eats.*".toRegex(), BalanceType.MEAL, 10),
    MerchantRule("(?i).*uber trip.*".toRegex(), BalanceType.CASH, 10),
    MerchantRule("(?i).*ifood.*".toRegex(), BalanceType.MEAL, 10),
    MerchantRule("(?i).*uber.*".toRegex(), BalanceType.CASH, 9),
    MerchantRule("(?i).*padaria.*".toRegex(), BalanceType.FOOD, 8),
    MerchantRule("(?i).*mercado.*".toRegex(), BalanceType.FOOD, 8),
    MerchantRule("(?i).*restaurante.*".toRegex(), BalanceType.MEAL, 8),
    MerchantRule("(?i).*churrascaria.*".toRegex(), BalanceType.MEAL, 8),
    MerchantRule("(?i).*sabor.*".toRegex(), BalanceType.MEAL, 6),
    MerchantRule("(?i).*picpay.*".toRegex(), BalanceType.CASH, 5),
    MerchantRule("(?i).*pag.*".toRegex(), BalanceType.MEAL, 5),
)