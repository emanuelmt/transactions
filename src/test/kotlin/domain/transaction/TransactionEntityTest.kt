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
                Arguments.arguments(defaultTransaction.copy(merchantTransactionType = "321"), BalanceType.CASH),
                Arguments.arguments(defaultTransaction.copy(merchantTransactionType = "5411"), BalanceType.FOOD),
                Arguments.arguments(defaultTransaction.copy(merchantTransactionType = "5412"), BalanceType.FOOD),
                Arguments.arguments(defaultTransaction.copy(merchantTransactionType = "5811"), BalanceType.MEAL),
                Arguments.arguments(defaultTransaction.copy(merchantTransactionType = "5812"), BalanceType.MEAL)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("getTransactionBalanceTypeTestCases")
    fun testGetTransactionBalanceTypeParam(transaction: TransactionEntity, expectedBalanceType: BalanceType) {
        val result = getTransactionBalanceType(transaction)
        assertEquals(expectedBalanceType, result, "Test failed for transaction: $transaction")
    }
}
