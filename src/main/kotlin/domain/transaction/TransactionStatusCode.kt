package dev.emanuelmt.domain.transaction

import kotlinx.serialization.Serializable

@Serializable(with = TransactionStatusCodeSerializer::class)
data class TransactionStatusCode(val code: String) {
    override fun toString(): String = code

    companion object {
        val InsufficientBalance: TransactionStatusCode = TransactionStatusCode("51")
        val RejectedTransaction: TransactionStatusCode = TransactionStatusCode("07")
        val ApprovedTransaction: TransactionStatusCode = TransactionStatusCode("00")
    }
}