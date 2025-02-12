package dev.emanuelmt.domain.wallet

enum class BalanceType {
    FOOD, MEAL, CASH
}

val FallbackBalanceTye = BalanceType.CASH

fun isFallbackBalanceType(balanceType: BalanceType): Boolean {
    return balanceType === FallbackBalanceTye
}