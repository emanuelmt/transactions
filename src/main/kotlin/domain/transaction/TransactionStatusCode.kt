package dev.emanuelmt.domain.transaction

import kotlinx.serialization.Serializable

@Serializable(with = TransactionStatusCodeSerializer::class)
data class TransactionStatusCode(val code: String) {
    override fun toString(): String = code

    public companion object {
        public val InsufficientBalance: TransactionStatusCode = TransactionStatusCode("51")
        public val RejectedTransaction: TransactionStatusCode = TransactionStatusCode("07")
        public val ApprovedTransaction: TransactionStatusCode = TransactionStatusCode("00")
    }
}