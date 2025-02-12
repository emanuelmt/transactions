package dev.emanuelmt.domain.transaction

import dev.emanuelmt.domain.wallet.BalanceType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class TransactionEntityTest {
    companion object {
        @JvmStatic
        fun getTransactionBalanceTypeTestCases(): Stream<Arguments> {
            val defaultTransaction = TransactionEntity("123", "123", "123", "123", 15000)
            return Stream.of(
                Arguments.arguments(defaultTransaction, BalanceType.CASH),
                Arguments.arguments(defaultTransaction.copy(merchantType = "321"), BalanceType.CASH),
                Arguments.arguments(defaultTransaction.copy(merchantType = "5411"), BalanceType.FOOD),
                Arguments.arguments(defaultTransaction.copy(merchantType = "5412"), BalanceType.FOOD),
                Arguments.arguments(defaultTransaction.copy(merchantType = "5811"), BalanceType.MEAL),
                Arguments.arguments(defaultTransaction.copy(merchantType = "5812"), BalanceType.MEAL)
            )
        }

        @JvmStatic
        fun getMerchantNameExamples(): Stream<Arguments> {
            val default = TransactionEntity("123", "123", "123", "123", 15000)
            return Stream.of(
                Arguments.arguments(default.copy(merchantName = "UBER EATS São Paulo", merchantType = "0000"), BalanceType.MEAL),
                Arguments.arguments(default.copy(merchantName = "UBER TRIP Campinas", merchantType = "9999"), BalanceType.CASH),
                Arguments.arguments(default.copy(merchantName = "UBER Campinas", merchantType = "9999"), BalanceType.CASH),
                Arguments.arguments(default.copy(merchantName = "Padaria do Zé", merchantType = "5411"), BalanceType.FOOD),
                Arguments.arguments(default.copy(merchantName = "Ifood", merchantType = "5411"), BalanceType.MEAL),
                Arguments.arguments(default.copy(merchantName = "PICPAY*PADARIABOMSABOR", merchantType = "5411"), BalanceType.FOOD),
                Arguments.arguments(default.copy(merchantName = "PICPAY*SaborDaRoca", merchantType = "5411"), BalanceType.MEAL),
                Arguments.arguments(default.copy(merchantName = "PICPAY*Sabor", merchantType = "5411"), BalanceType.MEAL),
                Arguments.arguments(default.copy(merchantName = "Restaurante Central", merchantType = "5811"), BalanceType.MEAL),
                Arguments.arguments(default.copy(merchantName = "Mercado Novo", merchantType = "9999"), BalanceType.FOOD),
                Arguments.arguments(default.copy(merchantName = "Teste de fallback", merchantType = "5812"), BalanceType.MEAL),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("getTransactionBalanceTypeTestCases")
    fun testGetTransactionBalanceTypeParam(transaction: TransactionEntity, expectedBalanceType: BalanceType) {
        val result = getTransactionBalanceType(transaction)
        assertEquals(expectedBalanceType, result, "Test failed for transaction: $transaction")
    }

    @ParameterizedTest
    @MethodSource("getMerchantNameExamples")
    fun testGetTransactionBalanceTypeByMerchantParam(transaction: TransactionEntity, expectedBalanceType: BalanceType) {
        val balanceType = merchantNameToBalanceType(transaction.merchantName, transaction.merchantType)
        assertEquals(expectedBalanceType, balanceType, "Failed with merchant: ${transaction.merchantName}")
    }

}
