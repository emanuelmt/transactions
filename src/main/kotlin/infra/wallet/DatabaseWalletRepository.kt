package dev.emanuelmt.infra.wallet

import dev.emanuelmt.domain.wallet.BalanceType
import dev.emanuelmt.domain.wallet.WalletEntity
import dev.emanuelmt.domain.wallet.WalletRepository
import dev.emanuelmt.infra.application.DatabaseRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object Wallets : Table() {
    val id = varchar("id", length = 36)
    val accountId = varchar("accountId", length = 36)
    val type = enumeration<BalanceType>("type")
    val balance = integer("balance")

    override val primaryKey = PrimaryKey(id)
}


class DatabaseWalletRepository(private val database: Database) : WalletRepository, DatabaseRepository() {

    init {
        transaction(this.database) {
            SchemaUtils.create(Wallets)
        }
    }

    override suspend fun save(wallet: WalletEntity) {
        dbQuery {
            Wallets.insert {
                it.fromWallet(wallet)
            }
        }
    }

    override suspend fun update(wallet: WalletEntity) {
        dbQuery {
            Wallets.update({Wallets.id eq wallet.id}) {
                it.fromWallet(wallet)
            }
        }
    }

    override suspend fun saveBatch(wallets: List<WalletEntity>) {
        dbQuery {
            Wallets.batchInsert(wallets) { wallet ->
                this.fromWallet(wallet)
            }
        }
    }

    override suspend fun find(accountId: String, type: BalanceType): WalletEntity? {
        return dbQuery {
            Wallets.selectAll()
                .where { (Wallets.accountId eq accountId) and (Wallets.type eq type) }
                .map { it.toWallet() }
                .singleOrNull()
        }
    }

    override suspend fun show(accountId: String): List<WalletEntity> {
        return dbQuery {
            Wallets.selectAll()
                .where { Wallets.accountId eq accountId }
                .map { it.toWallet() }
                .toList()
        }
    }
}

fun ResultRow.toWallet(): WalletEntity {
    return WalletEntity(this[Wallets.id], this[Wallets.balance], this[Wallets.type], this[Wallets.accountId])
}

fun UpdateBuilder<Int>.fromWallet(wallet: WalletEntity) {
    this[Wallets.id] = wallet.id
    this[Wallets.accountId] = wallet.accountId
    this[Wallets.type] = wallet.type
    this[Wallets.balance] = wallet.balance
}