package dev.emanuelmt.infra.application

import dev.emanuelmt.infra.account.DatabaseAccountRepository
import dev.emanuelmt.infra.wallet.DatabaseWalletRepository
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class DatabaseRepository() {
    inline fun <reified T : DatabaseRepository> instantiate(app: Application) {
        val repositoryKey = AttributeKey<T>(this::class.qualifiedName!!)
        app.attributes.put(repositoryKey, this as T)
    }

    protected suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.configureRepositories() {
    DatabaseWalletRepository(this.attributes[DatabaseConnectionKey]).instantiate<DatabaseWalletRepository>(this)
    DatabaseAccountRepository(this.attributes[DatabaseConnectionKey]).instantiate<DatabaseAccountRepository>(this)
}

inline fun <reified T : DatabaseRepository> Application.getRepository(): T {
    val key = AttributeKey<T>(T::class.qualifiedName ?: throw RuntimeException("Invalid repository class ${T::class}"))
    return this.attributes.getOrNull(key) ?: throw RuntimeException("Uninstantiated repository class ${T::class}")
}
