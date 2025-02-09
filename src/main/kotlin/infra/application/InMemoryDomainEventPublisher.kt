package dev.emanuelmt.infra.application

import dev.emanuelmt.domain.application.DomainEvent
import dev.emanuelmt.domain.application.DomainEventPublisher

class InMemoryDomainEventPublisher : DomainEventPublisher {
    private val handlers: MutableMap<Class<out DomainEvent>, MutableList<suspend (DomainEvent) -> Unit>> = mutableMapOf()

    override fun <T : DomainEvent> subscribe(eventType: Class<T>, handler: suspend (T) -> Unit) {
        val eventHandlers = handlers.getOrPut(eventType) { mutableListOf() }
        eventHandlers.add { event -> handler(event as T) }
    }

    override suspend fun publish(event: DomainEvent) {
        handlers[event::class.java]?.forEach { handler ->
            handler(event)
        }
    }
}