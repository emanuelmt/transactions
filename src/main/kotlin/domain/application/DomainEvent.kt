package dev.emanuelmt.domain.application

interface DomainEvent

interface DomainEventPublisher {
    suspend fun publish(event: DomainEvent)
    fun <T : DomainEvent> subscribe(eventType: Class<T>, handler: suspend (T) -> Unit)
}