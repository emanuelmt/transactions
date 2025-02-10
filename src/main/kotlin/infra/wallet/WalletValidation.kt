package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.account.isValidAccountId
import dev.emanuelmt.domain.wallet.AddWalletBalanceInput
import dev.emanuelmt.domain.wallet.CreateWalletInput
import io.ktor.server.plugins.requestvalidation.*

fun RequestValidationConfig.walletValidation() {
    validate<CreateWalletInput> { input: CreateWalletInput ->
        if (input.initialBalance < 0)
            ValidationResult.Invalid("initial balance should be greater than or equals 0")
        else ValidationResult.Valid
    }
    validate< AddWalletBalanceInput> { input: AddWalletBalanceInput ->
        if (input.balance < 0)
            ValidationResult.Invalid("balance value should be greater than 0")
        else if (!isValidAccountId(input.accountId))
            ValidationResult.Invalid("invalid account id")
        else
            ValidationResult.Valid

    }
}