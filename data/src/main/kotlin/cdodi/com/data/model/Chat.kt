@file:Suppress("unused")

package cdodi.com.data.model


import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object Chats : LongIdTable() {
    val name = varchar("name", 50)
    val ownerID = reference("ownerID", Users)
}

class Chat(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Chat>(Chats)

    var name by Chats.name
    val ownerID by User referrersOn Users.id
}
