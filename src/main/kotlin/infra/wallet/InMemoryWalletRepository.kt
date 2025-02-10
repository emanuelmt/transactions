package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.wallet.BalanceType
import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class InMemoryWalletRepository(private val database: Database) : WalletRepository {

    object Wallets : Table() {
        val id = varchar("id", length = 36)
        val accountId = varchar("accountId", length = 36)
        val type = enumeration<BalanceType>("type")
        val balance = integer("balance")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(this.database) {
            SchemaUtils.create(Wallets)
        }
    }

    override suspend fun save(wallet: WalletEntity) {
        dbQuery {
            Wallets.insert {
                it[id] = wallet.id
                it[accountId] = wallet.accountId
                it[type] = wallet.type
                it[balance] = wallet.balance
            }
        }
    }

    override suspend fun saveBatch(wallets: List<WalletEntity>) {
        dbQuery {
            Wallets.batchInsert(wallets) { wallet ->
                this[Wallets.id] = wallet.id
                this[Wallets.accountId] = wallet.accountId
                this[Wallets.type] = wallet.type
                this[Wallets.balance] = wallet.balance
            }
        }
    }

    override suspend fun show(accountId: String): List<WalletEntity> {
        return dbQuery {
            Wallets.selectAll()
                .where { Wallets.accountId eq accountId }
                .map { WalletEntity(it[Wallets.id], it[Wallets.balance], it[Wallets.type], it[Wallets.accountId]) }
                .toList()
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
