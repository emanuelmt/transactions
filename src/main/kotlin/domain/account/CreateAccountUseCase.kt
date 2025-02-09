package dev.emanuelmt.domain.account

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateAccountInput(val name: String)

@Serializable
data class CreateAccountOutput(val id: String, val name: String)

class CreateAccountUseCase(private val repository: AccountRepository) {
    suspend fun execute(input: CreateAccountInput): CreateAccountOutput {
        val account = AccountEntity(UUID.randomUUID().toString(), input.name)

        this.repository.save(account)

        return CreateAccountOutput(account.id, account.name)
    }
}