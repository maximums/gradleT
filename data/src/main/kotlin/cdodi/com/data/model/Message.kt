@file:Suppress("unused")

package cdodi.com.data.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object Messages : LongIdTable() {
    var body = text("body", eagerLoading = true)
    //    val sender = reference("senderID", Users)
    //    val receiver = reference("receiverID", Chats)
    val sender = long("senderID")
    val receiver = long("receiverID")
    val timestamp = long("timestamp")
    var isRead = bool("isRead").default(false)
}

class Message(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Message>(Messages)

    var body by Messages.body
    var sender by Messages.sender
    var receiver by Messages.receiver
    var timestamp by Messages.timestamp
    var isRead by Messages.isRead
}
