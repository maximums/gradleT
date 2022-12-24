package cdodi.com.data.services

import cdodi.com.data.DatabaseFactory.dbQuery
import com.cdodi.data.*
import cdodi.com.data.model.Message
import cdodi.com.data.model.Messages
import cdodi.com.data.model.toMessageResponse
import cdodi.com.data.model.toMessageRpc
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class MessageService : MessageServiceGrpcKt.MessageServiceCoroutineImplBase() {
    override suspend fun createMessage(request: MessageRequest): MessageResponse =
        if (isUserRegistered(request.receiverId))
            dbQuery {
                Message.new {
                    body = request.content
                    sender = request.senderId
                    receiver = request.receiverId
                    timestamp = Date().time
                    isRead = request.isRead
                }.toMessageResponse()
            }
        else MessageResponse.newBuilder().setIsNull(true).build()

    override suspend fun deleteMessage(request: MessageId): MessageDeleteResult = dbQuery {
        val isDeleted = Messages.deleteWhere { Messages.id eq request.id } > 0
        MessageDeleteResult.newBuilder().setIsDeleted(isDeleted).build()
    }

    override suspend fun editMessage(request: MessageEditRequest): MessageResponse =
        if (isMessagePresent(request.id))
            dbQuery {
                val msg = Message[request.id]
                msg.body = request.content
                msg.toMessageResponse()
            }
        else MessageResponse.newBuilder().setIsNull(true).build()

    override suspend fun getMessage(request: MessageId): MessageResponse = dbQuery {
        Message.find { Messages.id eq request.id }.firstOrNull().toMessageResponse()
    }

    override suspend fun getAllMessages(request: AllMessageRequest): AllMessageResponse = dbQuery {
        val messages = Message.all().limit(50, request.offset).map(Message::toMessageRpc)
        AllMessageResponse.newBuilder().addAllMessages(messages).build()
    }
}

suspend fun isMessagePresent(id: Long): Boolean = dbQuery {
    Message.find { Messages.id eq id }.count() > 0
}