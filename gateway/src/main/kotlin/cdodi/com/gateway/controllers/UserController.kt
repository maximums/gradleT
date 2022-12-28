package cdodi.com.gateway.controllers

import cdodi.com.gateway.Email
import cdodi.com.gateway.toRestUserResponse
import com.cdodi.data.AllUsersRequest
import com.cdodi.data.MessageServiceGrpcKt
import com.cdodi.data.User
import com.cdodi.data.UserServiceGrpcKt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

// TODO: will add arrow to fix this, hope I can

data class ChatSession(val chat: String, val session: DefaultWebSocketServerSession)

val connections = Collections.synchronizedSet<ChatSession>(LinkedHashSet())

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
        connections += ChatSession(chat, this)
        send("You are connected to chat:= $chat")
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            connections.forEach {
                if (it.chat == chat) it.session.send(receivedText)
            }
        }
    }
}