package dev.emanuelmt.domain.account

import dev.emanuelmt.domain.application.DomainEventPublisher
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateAccountInput(val name: String)

@Serializable
data class CreateAccountOutput(val id: String, val name: String)

class CreateAccountUseCase(private val repository: AccountRepository, private val createdEventPublisher: DomainEventPublisher) {
    suspend fun execute(input: CreateAccountInput): CreateAccountOutput {
        val account = AccountEntity(UUID.randomUUID().toString(), input.name)

        this.repository.save(account)

        this.createdEventPublisher.publish(AccountCreatedEvent(account))

        return CreateAccountOutput(account.id, account.name)
    }
}