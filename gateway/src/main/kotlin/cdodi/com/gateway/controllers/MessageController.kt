package cdodi.com.gateway.controllers

import arrow.core.continuations.either
import arrow.core.getOrElse
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
        val principal = call.principal<JWTPrincipal>() ?: return@get
        val userEmail = principal.payload.getClaim("email").asString()
        val user = userService.getUser(Email { email = userEmail }).toRestUserResponse()

        val result = either {
            val msgId = call.parameters.getEither("id").bind().toLong()

            messageService.getMessage(
                MessageGetReq {
                    id = msgId
                    senderId = user.bind().id
                }
            ).toRestMessageResponse()
        }

        restCall(result)

//        val resp = messageService.getMessage(
//            MessageGetReq {
//                id = msgId
//                senderId = user.id
//            }
//        ).toRestMessageResponse()
//        if (resp == null) {
//            call.respond(HttpStatusCode.BadRequest, "Message with id: '$msgId' not found")
//            return@get
//        }
//
//        call.respond(HttpStatusCode.OK, resp)
    }

    get("/all/{offset?}") {
        val offset = call.parameters.getEither("offset").getOrElse { "0" }.toLong()

        val resp = messageService.getAllMessages(
            AllMessageRequest.newBuilder().setOffset(offset).build()
        ).messagesList.map(Message::toRestMessageResponse)

        call.respond(HttpStatusCode.OK, resp)
    }

    post("/add") {
        val userEmail = call.principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString()
        val userId = userService.getUser(Email { email = userEmail }).user.id

        val newMessage = call.receive<RestMessageRequest>().toMessageRequest(senderId = userId)
        val response = messageService.createMessage(newMessage).toRestMessageResponse()

        call.respond(HttpStatusCode.OK, response)
    }

    delete("/del/{id}") {
        val principal = call.principal<JWTPrincipal>() ?: return@delete
        val userEmail = principal.payload.getClaim("email").asString()
        val user = userService.getUser(Email { email = userEmail }).toRestUserResponse()

        val result = either {
            val msgId = call.parameters.getEither("id").bind().toLong()
            messageService.deleteMessage(
                MessageGetReq {
                    id = msgId
                    senderId = user.bind().id
                }
            )
        }

        restCall(result)

//        messageService.deleteMessage(
//            MessageGetReq {
//                id = msgId
//                senderId = user.id
//            }
//        )
//
//        call.respond(HttpStatusCode.OK, "Message was successfully deleted")
    }

    patch("/edit") {
        val newMsg = call.receive<RestEditMessage>()

        val response = messageService.editMessage(
            MessageEditRequest {
                id = newMsg.id
                content = newMsg.content
            }
        ).toRestMessageResponse()

        restCall(response)
    }
}