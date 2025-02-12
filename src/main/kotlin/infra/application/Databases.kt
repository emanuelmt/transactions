package dev.emanuelmt.infra.application

import io.ktor.server.application.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database

val DatabaseConnectionKey = AttributeKey<Database>("DatabaseConnection")

fun Application.configureDatabases() {
    val host = System.getenv("POSTGRES_HOST") ?: "localhost"
    val port = System.getenv("POSTGRES_PORT") ?: "5432"
    val dbName = System.getenv("POSTGRES_DB") ?: "default_db"
    val user = System.getenv("POSTGRES_USER") ?: "default_user"
    val password = System.getenv("POSTGRES_PASSWORD") ?: "default_password"

    val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName"

    val database = Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    this.attributes.put(DatabaseConnectionKey, database)
}
