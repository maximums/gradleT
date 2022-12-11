package cdodi.com.data

import cdodi.com.data.model.Chats
import cdodi.com.data.model.Messages
import cdodi.com.data.model.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val databaseUri = "jdbc:postgresql://db:5432/ktorjournal?user=postgres"

        val database = Database.connect(databaseUri, driverClassName)
        transaction(database) {
            SchemaUtils.create(Users, Chats, Messages)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}