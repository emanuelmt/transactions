package dev.emanuelmt.infra.application

import io.ktor.server.application.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database

val DatabaseConnectionKey = AttributeKey<Database>("DatabaseConnection")

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    this.attributes.put(DatabaseConnectionKey, database)
}
