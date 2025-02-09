package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.wallet.BalanceType
import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InMemoryWalletRepository : WalletRepository {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    object Wallets : Table() {
        val id = varchar("id", length = 36)
        val accountId = varchar("accountId", length = 36)
        val type = enumeration<BalanceType>("type")
        val balance = integer("balance")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Wallets)
        }
    }

    override suspend fun save(wallet: WalletEntity) {
        Wallets.insert {
            it[id] = wallet.id
            it[accountId] = wallet.accountId
            it[type] = wallet.type
            it[balance] = wallet.balance
        }
    }

    override suspend fun saveBatch(wallets: List<WalletEntity>) {
        transaction {
            Wallets.batchInsert(wallets) { wallet ->
                this[Wallets.id] = wallet.id
                this[Wallets.accountId] = wallet.accountId
                this[Wallets.type] = wallet.type
                this[Wallets.balance] = wallet.balance
            }
        }
    }
}
