package dev.emanuelmt.domain.transaction

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TransactionStatusCodeSerializer : KSerializer<TransactionStatusCode> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TransactionStatusCode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TransactionStatusCode) {
        encoder.encodeString(value.code)
    }

    override fun deserialize(decoder: Decoder): TransactionStatusCode {
        val code = decoder.decodeString()
        return TransactionStatusCode(code)
    }
}
