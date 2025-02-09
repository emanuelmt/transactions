package dev.emanuelmt.infra.account

import dev.emanuelmt.domain.account.AccountEntity
import dev.emanuelmt.domain.account.AccountRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InMemoryAccountRepository : AccountRepository {
   private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    object Accounts : Table() {
        val id = varchar("id", length = 36)
        val name = varchar("name", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Accounts)
        }
    }

    override suspend fun save(account: AccountEntity) {
        Accounts.insert {
            it[id] = account.id
            it[name] = account.name
        }
    }
}
