package dev.emanuelmt.domain.account

import dev.emanuelmt.domain.application.DomainEvent

data class AccountCreatedEvent (val account: AccountEntity) : DomainEvent