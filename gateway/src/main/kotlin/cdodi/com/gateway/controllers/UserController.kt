package cdodi.com.gateway.controllers

import arrow.core.*
import arrow.core.continuations.either
import cdodi.com.gateway.*
import cdodi.com.gateway.dto.RestMessage
import cdodi.com.gateway.dto.RestMessageRequest
import cdodi.com.gateway.dto.RestMessageResponse
import cdodi.com.gateway.dto.RestUserResponse
import com.cdodi.data.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

data class ChatSession(val chat: String, val session: DefaultWebSocketServerSession)

private val connections: MutableSet<ChatSession> = Collections.synchronizedSet(LinkedHashSet())

sealed class ErrorResponse {
    object MissingParam : ErrorResponse()
    object Another : ErrorResponse()
}

fun Parameters.getEither(name: String): Either<String, String> {
    val value = this[name]

    return if (value != null) Either.Right(value)
    else Either.Left("Missing parameter $name")
}

inline fun <reified T: Principal> ApplicationCall.getPrincipal(): Either<String, T> {
    val principal = this.principal<T>()

    return if (principal != null) Either.Right(principal)
    else Either.Left("Something went wrong with authentication")
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.restCall(result: Either<String, T>) =
    when (result) {
        is Either.Right -> call.respond(HttpStatusCode.OK, result.value)
        is Either.Left -> call.respond(HttpStatusCode.BadRequest, result.value)
    }

fun Route.userRoutes(
    msgStub: MessageServiceGrpcKt.MessageServiceCoroutineStub,
    service: UserServiceGrpcKt.UserServiceCoroutineStub
) {
    get("/{email}") {
        val userEmail = call.parameters.getEither("email")
        val resp = either {
            service.getUser(
                Email { email = userEmail.bind() }
            ).toRestUserResponse().bind()
        }

        restCall(resp)
    }

    get("/all/{offset?}") {
        val offset = call.parameters.getEither("offset").getOrElse { "0" }.toLong()

        val resp = service.getAllUsers(
            AllUsersRequest.newBuilder().setOffset(offset).build()
        ).usersList.map(User::toRestUserResponse)

        call.respond(HttpStatusCode.OK, resp)
    }

    delete("/del/{email}") {
        when (val userEmail = call.parameters.getEither("email")) {
            is Either.Right -> {
                service.deleteUser(Email { email = userEmail.value })
                call.respond(HttpStatusCode.OK, "User with email:${userEmail.value} was successfully deleted")
            }
            is Either.Left -> call.respond(HttpStatusCode.BadRequest, userEmail.value)
        }

//        val result = either {
//            val userEmail = call.parameters.getEither("email").bind()
//            service.deleteUser(Email { email = userEmail })
//        }
//
//        restCall(result)
    }

    webSocket("/chat/{id}") {
        val result = either {
            val chat = call.parameters.getEither("id").bind()
            val principal = call.getPrincipal<JWTPrincipal>().bind()
            val userEmail = principal.payload.getClaim("email").asString()
            val user = service.getUser(Email { email = userEmail }).toRestUserResponse().bind()
            val initMessages = msgStub.getAllChatMessages(ChatMessages {
                chatId = chat.toLong()
                offset = 0
            }).messagesList.map(Message::toRestMessage)

            connections += ChatSession(chat, this@webSocket)

            if (initMessages.isNotEmpty()) initMessages.forEach { sendSerialized(it) }

            for (frame in incoming) {
                val receivedMsg = converter?.deserialize<RestMessageRequest>(frame) ?: return@either
                val msg = msgStub.createMessage(receivedMsg.toMessageRequest(user.id))
                connections.forEach {
                    if (it.chat == chat) it.session.sendSerialized(msg.toRestMessage())
                }
            }
        }
//        val principal = call.getPrincipal<JWTPrincipal>()
//        val userEmail = principal.payload.getClaim("email").asString()

//        val chat = call.parameters.getEither("id")

//        val user = service.getUser(Email { email = userEmail }).toRestUserResponse()
//        val initMessages = msgStub.getAllChatMessages(ChatMessages {
//            chatId = chat.toLong()
//            offset = 0
//        }).messagesList.map(Message::toRestMessage)
//
//        connections += ChatSession(chat, this)
//
//        if (initMessages.isNotEmpty()) initMessages.forEach { sendSerialized(it) }
//
//        for (frame in incoming) {
//            val receivedMsg = converter?.deserialize<RestMessageRequest>(frame) ?: return@webSocket
//            val msg = msgStub.createMessage(receivedMsg.toMessageRequest(user.id))
//            connections.forEach {
//                if (it.chat == chat) it.session.sendSerialized(msg.toRestMessage())
//            }
//        }
    }
}
