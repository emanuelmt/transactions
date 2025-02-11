package dev.emanuelmt.domain.transaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionAuthorizationInput(
    @SerialName("account")
    val accountId: String,
    @SerialName("totalAmount")
    val amount: Int,
    @SerialName("mcc")
    val merchantType: String,
    @SerialName("merchant")
    val merchantName: String
)

@Serializable
data class TransactionAuthorizationOutput(val code: TransactionStatusCode)