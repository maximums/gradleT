package cdodi.com.gateway.controllers

import cdodi.com.gateway.*
import cdodi.com.gateway.dto.RestEditMessage
import cdodi.com.gateway.dto.RestMessageRequest
import com.cdodi.data.AllMessageRequest
import com.cdodi.data.Message
import com.cdodi.data.MessageServiceGrpcKt
import com.cdodi.data.UserServiceGrpcKt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.messageRoutes(
    messageService: MessageServiceGrpcKt.MessageServiceCoroutineStub,
    userService: UserServiceGrpcKt.UserServiceCoroutineStub
) {
    get("/{id}") {
        val msgId = call.parameters["id"]?.toLong()
        if (msgId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing parameter 'id'")
            return@get
        }

        val resp = messageService.getMessage(MessageId { id = msgId }).toRestMessageResponse()
        if (resp == null) {
            call.respond(HttpStatusCode.BadRequest, "Message with id: '$msgId' not found")
            return@get
        }

        call.respond(HttpStatusCode.OK, resp)
    }

    get("/all/{offset}") {
        val offset = call.parameters["offset"]?.toLong() ?: 0
        val resp = messageService.getAllMessages(
            AllMessageRequest.newBuilder().setOffset(offset).build()
        ).messagesList.map(Message::toRestMessageResponse)

        call.respond(HttpStatusCode.OK, resp)
    }

    post("/add") {
        val userEmail = call.principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString()
        val userId = userService.getUser(Email { email = userEmail }).user.id

        val newMessage = call.receive<RestMessageRequest>().toMessageRequest(senderId = userId)
        // TODO: need to remove nullability because user will exist always[data:MessageService.createMessage]
        val response = messageService.createMessage(newMessage).toRestMessageResponse() ?: return@post

        call.respond(HttpStatusCode.OK, response)
    }

    delete("/del/{id}") {
        val msgId = call.parameters["id"]?.toLong()
        if (msgId == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing parameter 'id'")
            return@delete
        }

        val msg = messageService.getMessage(MessageId { id = msgId }).toRestMessageResponse()
        if (msg == null) {
            call.respond(HttpStatusCode.BadRequest, "Message with id: '$msgId' not found")
            return@delete
        }
        // TODO: need to refactor service[message can be deleted only by owner]
        messageService.deleteMessage(MessageId { id = msgId })

        call.respond(HttpStatusCode.OK, "Message [${msg.content}] was successfully deleted")
    }

    patch("/edit") {
        val newMsg = call.receive<RestEditMessage>()
        val response = messageService.editMessage(
            MessageEditRequest {
                id = newMsg.id
                content = newMsg.content
            }
        ).toRestMessageResponse()

        if (response == null) {
            call.respond(HttpStatusCode.BadRequest, "Message with id: '${newMsg.id}' not found")
            return@patch
        }

        call.respond(HttpStatusCode.OK, response)
    }
}