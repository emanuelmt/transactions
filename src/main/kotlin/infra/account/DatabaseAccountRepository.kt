package dev.emanuelmt.infra.account

import dev.emanuelmt.domain.account.AccountEntity
import dev.emanuelmt.domain.account.AccountRepository
import dev.emanuelmt.infra.application.DatabaseRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseAccountRepository(private val database: Database) : AccountRepository, DatabaseRepository() {

    object Accounts : Table() {
        val id = varchar("id", length = 36)
        val name = varchar("name", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(this.database) {
            SchemaUtils.create(Accounts)
        }
    }

    override suspend fun save(account: AccountEntity) {
        dbQuery {
            Accounts.insert {
                it[id] = account.id
                it[name] = account.name
            }
        }
    }
}
