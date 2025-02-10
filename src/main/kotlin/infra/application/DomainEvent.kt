package dev.emanuelmt.infra.application

import dev.emanuelmt.domain.application.DomainEventPublisher
import io.ktor.server.application.*
import io.ktor.util.*

val DomainEventPublisherKey = AttributeKey<DomainEventPublisher>("DomainEventPublisher")

fun Application.configureDomainEvents() {
    val publisher = InMemoryDomainEventPublisher()
    this.attributes.put(DomainEventPublisherKey, publisher)
}
