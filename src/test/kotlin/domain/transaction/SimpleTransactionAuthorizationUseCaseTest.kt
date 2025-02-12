package dev.emanuelmt.domain.transaction


import dev.emanuelmt.domain.MEMORY_DATABASE_CONN
import dev.emanuelmt.domain.wallet.BalanceType
import dev.emanuelmt.domain.wallet.addBalance
import dev.emanuelmt.domain.wallet.newWallet
import dev.emanuelmt.infra.transaction.DatabaseTransactionRepository
import dev.emanuelmt.infra.transaction.Transactions
import dev.emanuelmt.infra.wallet.DatabaseWalletRepository
import dev.emanuelmt.infra.wallet.Wallets
import dev.emanuelmt.infra.wallet.toWallet
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleTransactionAuthorizationUseCaseTest {
    private lateinit var useCase: SimpleTransactionAuthorizationUseCase
    private var walletRepository = spyk(DatabaseWalletRepository(MEMORY_DATABASE_CONN), recordPrivateCalls = true)
    private var transactionRepository =
        spyk(DatabaseTransactionRepository(MEMORY_DATABASE_CONN), recordPrivateCalls = true)

    @BeforeAll
    fun initAll() {
        mockkStatic(UUID::class)
        this.useCase = SimpleTransactionAuthorizationUseCase(transactionRepository, walletRepository)
    }

    @BeforeEach
    fun beforeEach() {
        transaction {
            Wallets.deleteAll()
            Transactions.deleteAll()
        }
        clearAllMocks()
    }

    @Test
    fun `should sub balance from wallet based on merchantType and return approved transaction`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"
        val mockedWalletId = "ac86166f-2463-46c8-afa7-1915aa027170"
        every { UUID.randomUUID() } returns UUID.fromString(mockedWalletId)
        val wallet = newWallet(BalanceType.FOOD, mockedAccountId).addBalance(10000)
        walletRepository.save(wallet)

        val input = TransactionAuthorizationInput(mockedAccountId, 1500, "5411", "MERCHANT TEST")
        val result = useCase.execute(input)

        assertEquals(TransactionAuthorizationOutput(TransactionStatusCode.ApprovedTransaction), result)

        coVerify(exactly = 1) {
            walletRepository.update(match { updatedWallet ->
                updatedWallet.id == mockedWalletId && updatedWallet.balance == wallet.balance - 1500
            })
            transactionRepository.save(match { savedTransaction ->
                savedTransaction.walletId == wallet.id
            }, any())
        }
    }

    @Test
    fun `should returns rejected transaction if dont find wallet to MCC`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"

        val input = TransactionAuthorizationInput(mockedAccountId, 1500, "4131", "MERCHANT TEST")
        val result = useCase.execute(input)

        assertEquals(TransactionAuthorizationOutput(TransactionStatusCode.RejectedTransaction), result)
    }

    @Test
    fun `should return insufficient balance when transaction amount is greater than wallet balance`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"
        val mockedWalletId = "ac86166f-2463-46c8-afa7-1915aa027170"
        every { UUID.randomUUID() } returns UUID.fromString(mockedWalletId)
        val wallet = newWallet(BalanceType.FOOD, mockedAccountId).addBalance(10000)
        walletRepository.save(wallet)

        val input = TransactionAuthorizationInput(mockedAccountId, 15000, "5411", "MERCHANT TEST")
        val result = useCase.execute(input)

        assertEquals(TransactionAuthorizationOutput(TransactionStatusCode.InsufficientBalance), result)
    }

    @Test
    fun `should rollback if wallet update trows inside transaction save`() = runTest {
        val mockedAccountId = "3894eae4-216f-451a-b5a8-ca361a3597d7"
        val mockedWalletId = "ac86166f-2463-46c8-afa7-1915aa027170"
        every { UUID.randomUUID() } returns UUID.fromString(mockedWalletId)
        val wallet = newWallet(BalanceType.FOOD, mockedAccountId).addBalance(10000)
        walletRepository.save(wallet)

        coEvery { walletRepository.update(any()) } throws Exception("Simulated exception")

        val input = TransactionAuthorizationInput(mockedAccountId, 1500, "5411", "MERCHANT TEST")
        val result = useCase.execute(input)

        assertEquals(TransactionAuthorizationOutput(TransactionStatusCode.RejectedTransaction), result)

        val unUpdatedWallet = transaction {
            Wallets.selectAll().where { Wallets.id eq mockedWalletId }.map { it.toWallet() }.single()
        }
        assertEquals(wallet, unUpdatedWallet)

        assertNull(transaction{
            Transactions.selectAll().singleOrNull()
        })

        coVerify(exactly = 1) {
            walletRepository.update(match { updatedWallet ->
                updatedWallet.id == mockedWalletId && updatedWallet.balance == wallet.balance - 1500
            })
            transactionRepository.save(match { savedTransaction ->
                savedTransaction.walletId == wallet.id
            }, any())
        }
    }

}
