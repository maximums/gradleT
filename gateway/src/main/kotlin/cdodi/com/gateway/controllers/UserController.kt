package cdodi.com.gateway.controllers

import cdodi.com.gateway.*
import cdodi.com.gateway.dto.RestMessage
import cdodi.com.gateway.dto.RestMessageRequest
import cdodi.com.gateway.dto.RestMessageResponse
import com.cdodi.data.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

// TODO: will add arrow to fix this, hope I can

data class ChatSession(val chat: String, val session: DefaultWebSocketServerSession)

val connections: MutableSet<ChatSession> = Collections.synchronizedSet<ChatSession>(LinkedHashSet())

fun Route.userRoutes(
    msgStub: MessageServiceGrpcKt.MessageServiceCoroutineStub,
    service: UserServiceGrpcKt.UserServiceCoroutineStub
) {
    get("/{email}") {
        val userEmail = call.parameters["email"]
        if (userEmail == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing parameter 'email'")
            return@get
        }

        val resp = service.getUser(Email { email = userEmail }).toRestUserResponse()
        if (resp == null) {
            call.respond(HttpStatusCode.OK, "User with email:$userEmail not found")
            return@get
        }

        call.respond(HttpStatusCode.OK, resp)
    }

    get("/all/{offset}") {
        val offset = call.parameters["offset"]?.toLong() ?: 0
        val resp = service.getAllUsers(
            AllUsersRequest.newBuilder().setOffset(offset).build()
        ).usersList.map(User::toRestUserResponse)

        call.respond(HttpStatusCode.OK, resp)
    }

    delete("/del/{email}") {
        val userEmail = call.parameters["email"]
        if (userEmail == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing parameter 'email'")
            return@delete
        }
        service.deleteUser(Email { email = userEmail })

        call.respond(HttpStatusCode.OK, "User with email:$userEmail was successfully deleted")
    }

    webSocket("/chat/{id}") {
        val chat = call.parameters["id"] ?: return@webSocket
        val principal = call.principal<JWTPrincipal>() ?: return@webSocket
        val userEmail = principal.payload.getClaim("email").asString()
        val user = service.getUser(Email { email = userEmail }).toRestUserResponse() ?: return@webSocket
        val initMessages = msgStub.getAllChatMessages(ChatMessages {
            chatId = chat.toLong()
            offset = 0
        }).messagesList.map(Message::toRestMessage)

        connections += ChatSession(chat, this)

        if (initMessages.isNotEmpty()) initMessages.forEach { sendSerialized(it) }

        for (frame in incoming) {
            val receivedMsg = converter?.deserialize<RestMessageRequest>(frame) ?: return@webSocket
            msgStub.createMessage(receivedMsg.toMessageRequest(user.id))
            connections.forEach {
                if (it.chat == chat) it.session.sendSerialized(receivedMsg)
            }
        }
    }
}
