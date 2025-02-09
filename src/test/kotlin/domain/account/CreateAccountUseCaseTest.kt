package dev.emanuelmt.domain.account


import dev.emanuelmt.domain.MEMORY_DATABASE_CONN
import dev.emanuelmt.infra.account.InMemoryAccountRepository
import dev.emanuelmt.infra.application.InMemoryDomainEventPublisher
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateAccountUseCaseTest {
    private lateinit var useCase: CreateAccountUseCase
    private var repository = spyk(InMemoryAccountRepository(MEMORY_DATABASE_CONN), recordPrivateCalls = true)
    private var eventPublisher = spyk(InMemoryDomainEventPublisher(), recordPrivateCalls = true)

    @BeforeAll
    fun initAll() {
        this.useCase = CreateAccountUseCase(repository, eventPublisher)
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun `should return correct account`() = runTest {
        val input = CreateAccountInput("Emanuel Marques")
        val result = useCase.execute(input)

        assertEquals(input.name, result.name)
    }

    @Test
    fun `should throw exception when repository save fails`() = runTest {
        coEvery { repository.save(any()) } throws Exception("Simulated exception")

        val input = CreateAccountInput("Emanuel Marques")

        assertFailsWith<Exception>("Simulated exception") {
            runBlocking {
                useCase.execute(input)
            }
        }
    }

    @Test
    fun `should throw exception when event publish fails`() = runTest {
        coEvery { eventPublisher.publish(any()) } throws Exception("Simulated exception")

        val input = CreateAccountInput("Emanuel Marques")

        assertFailsWith<Exception>("Simulated exception") {
            runBlocking {
                useCase.execute(input)
            }
        }
    }

}
