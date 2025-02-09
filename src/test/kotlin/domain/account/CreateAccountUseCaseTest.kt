package dev.emanuelmt.domain.account


import dev.emanuelmt.infra.account.InMemoryAccountRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateAccountUseCaseTest {
    private lateinit var useCase: CreateAccountUseCase
    private var repository: InMemoryAccountRepository = spyk(InMemoryAccountRepository(), recordPrivateCalls = true)

    @BeforeAll
    fun initAll() {
        this.useCase = CreateAccountUseCase(repository)
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun `should return correct account`() = runTest {
        newSuspendedTransaction {
            val input = CreateAccountInput("Emanuel Marques")
            val result = useCase.execute(input)

            assertEquals(input.name, result.name)
        }
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

}
