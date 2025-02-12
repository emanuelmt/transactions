package dev.emanuelmt.infra.transaction

import dev.emanuelmt.domain.transaction.TransactionEntity
import dev.emanuelmt.domain.transaction.TransactionRepository
import dev.emanuelmt.infra.application.DatabaseRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object Transactions : Table() {
    val id = varchar("id", length = 36)
    val walletId = varchar("walletId", length = 36)
    val merchantType = varchar("merchantTransactionType", length = 4)
    val merchantName = varchar("merchantName", length = 60)
    val amount = integer("amount")

    override val primaryKey = PrimaryKey(id)
}

class DatabaseTransactionRepository(private val database: Database) : TransactionRepository,
    DatabaseRepository() {

    init {
        transaction(this.database) {
            SchemaUtils.create(Transactions)
        }
    }

    override suspend fun save(transaction: TransactionEntity, updateWalletCall: suspend () -> Unit) {
        dbQuery {
            Transactions.insert {
                it.fromTransaction(transaction)
            }
            updateWalletCall()
        }
    }
}

fun UpdateBuilder<Int>.fromTransaction(transaction: TransactionEntity) {
    this[Transactions.id] = transaction.id
    this[Transactions.walletId] = transaction.walletId
    this[Transactions.merchantType] = transaction.merchantType
    this[Transactions.merchantName] = transaction.merchantName
    this[Transactions.amount] = transaction.amount
}