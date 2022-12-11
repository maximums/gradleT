@file:Suppress("unused")

package cdodi.com.data.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object Messages : LongIdTable() {
    val body = text("body", eagerLoading = true)
    val sender = reference("senderID", Users)
    val receiver = reference("receiverID", Chats)
    val timestamp = long("timestamp")
}

class Message(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Message>(Messages)

    var body by Messages.body
    val sender by User referrersOn Users.id
    val receiver by Chat referrersOn Chats.id
    var timestamp by Messages.timestamp
}
