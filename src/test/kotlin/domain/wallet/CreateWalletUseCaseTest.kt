package dev.emanuelmt.domain.wallet


import dev.emanuelmt.domain.MEMORY_DATABASE_CONN
import dev.emanuelmt.infra.wallet.InMemoryWalletRepository
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
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateWalletUseCaseTest {
    private lateinit var useCase: CreateWalletUseCase
    private var repository = spyk(InMemoryWalletRepository(MEMORY_DATABASE_CONN), recordPrivateCalls = true)

    @BeforeAll
    fun initAll() {
        this.useCase = CreateWalletUseCase(repository)
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun `should return correct account`() = runTest {
        newSuspendedTransaction {
            val input = CreateWalletInput("walletId", 15000, BalanceType.FOOD)
            val result = useCase.execute(input)

            assertNotNull(result)
        }
    }

    @Test
    fun `should throw exception when repository save fails`() = runTest {
        coEvery { repository.save(any()) } throws Exception("Simulated exception")

        val input = CreateWalletInput("walletId", 15000, BalanceType.FOOD)

        assertFailsWith<Exception>("Simulated exception") {
            runBlocking {
                useCase.execute(input)
            }
        }
    }

}
