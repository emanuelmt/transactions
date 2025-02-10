package dev.emanuelmt.domain.account

import io.ktor.server.plugins.requestvalidation.*
import java.util.*

data class AccountEntity(val id: String, val name: String)

fun validateAccountId(accountId: String?): String {
    val invalidException = RequestValidationException("Validation failed", listOf("invalid account id"))

    if (accountId == null) {
        throw invalidException
    }

    try {
        UUID.fromString(accountId)
    } catch (e: IllegalArgumentException) {
        throw invalidException
    }

    return accountId
}