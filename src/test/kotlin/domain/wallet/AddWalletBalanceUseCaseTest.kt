package dev.emanuelmt.domain.wallet


import dev.emanuelmt.domain.MEMORY_DATABASE_CONN
import dev.emanuelmt.infra.wallet.DatabaseWalletRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddWalletBalanceUseCaseTest {
    private lateinit var useCase: AddWalletBalanceUseCase
    private var repository = spyk(DatabaseWalletRepository(MEMORY_DATABASE_CONN), recordPrivateCalls = true)

    @BeforeAll
    fun initAll() {
        mockkStatic(UUID::class)
        this.useCase = AddWalletBalanceUseCase(repository)
    }

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Test
    fun `should add balance to existent wallet`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"
        val mockedWalletId = "ac86166f-2463-46c8-afa7-1915aa027170"
        every { UUID.randomUUID() } returns UUID.fromString(mockedWalletId)
        val wallet = newWallet(BalanceType.FOOD, mockedAccountId).addBalance(10000)
        repository.save(wallet)
        val balanceToAdd = 15000

        val input = AddWalletBalanceInput(mockedAccountId, balanceToAdd, BalanceType.FOOD)
        val result = useCase.execute(input)

        assertEquals(balanceToAdd + wallet.balance, result.balance)
        assertEquals(mockedWalletId, result.id)
        coVerify(exactly = 1) { repository.save(any()) }
        coVerify(exactly = 1) { repository.update(any()) }
    }

    @Test
    fun `should add balance to a new wallet`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"
        val mockedWalletId = "3a5bf287-54d9-426f-87e7-5ef6189514d0"
        every { UUID.randomUUID() } returns UUID.fromString(mockedWalletId)
        val balanceToAdd = 25000

        val input = AddWalletBalanceInput(mockedAccountId, balanceToAdd, BalanceType.MEAL)
        val result = useCase.execute(input)

        assertEquals(balanceToAdd, result.balance)
        assertEquals(mockedWalletId, result.id)
        coVerify(exactly = 1) { repository.save(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should throw exception when repository save fails`() = runTest {
        coEvery { repository.save(any()) } throws Exception("Simulated exception")

        val input = AddWalletBalanceInput("walletId", 15000, BalanceType.FOOD)

        assertFailsWith<Exception>("Simulated exception") {
            runBlocking {
                useCase.execute(input)
            }
        }
    }

}
