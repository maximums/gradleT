package cdodi.com.gateway.plugins

import cdodi.com.gateway.*
import cdodi.com.gateway.dto.RestEditMessage
import cdodi.com.gateway.dto.RestMessageRequest
import cdodi.com.gateway.dto.RestUserRequest
import com.cdodi.data.*
import com.cdodi.data.MessageId
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    val channel = ManagedChannelBuilder.forAddress("data-service", 8080).usePlaintext().build()
    val stub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)
    val msgStub = MessageServiceGrpcKt.MessageServiceCoroutineStub(channel)

    routing {
        get("/{email}") {
            val userEmail = call.parameters["email"] ?: return@get
            val resp = stub.getUser(Email { email = userEmail }).toRestUserResponse() ?: return@get
//            call.respond(
//                HttpStatusCode.OK,
//                "User with email:$userEmail not found"
//            )
            call.respond(HttpStatusCode.OK, resp)
        }

        get("/all") {
            val resp = stub.getAllUsers(
                AllUsersRequest.newBuilder().setOffset(0).build()
            ).usersList.map(User::toRestUserResponse)
            call.respond(HttpStatusCode.OK, resp)
        }

        post("/add") {
            val newUser: UserRequest = call.receive<RestUserRequest>().toUserRequest()
            val response = stub.createUser(newUser).toRestUserResponse() ?: return@post
//            call.respond(
//                HttpStatusCode.BadRequest,
//                "Unable to create new user"
//            )

            call.respond(HttpStatusCode.OK, response)
        }

        delete("/del/{email}") {
            val userEmail = call.parameters["email"] ?: return@delete
            stub.deleteUser(Email { email = userEmail })

            call.respond(HttpStatusCode.OK, "User with email:$userEmail was successfully deleted")
        }

        route("/msg") {
            get("/{id}") {
                val msgId = call.parameters["id"]?.toLong() ?: return@get
                val resp = msgStub.getMessage(MessageId { id = msgId }).toRestMessageResponse() ?: return@get
                call.respond(HttpStatusCode.OK, resp)
            }

            get("/all") {
                val resp = msgStub.getAllMessages(
                    AllMessageRequest.newBuilder().setOffset(0).build()
                ).messagesList.map(Message::toRestMessageResponse)
                call.respond(HttpStatusCode.OK, resp)
            }

            post("/add") {
                val newMessage = call.receive<RestMessageRequest>().toMessageRequest(senderId = 2)
                val response = msgStub.createMessage(newMessage).toRestMessageResponse() ?: return@post
                call.respond(HttpStatusCode.OK, response)
            }

            delete("/del/{id}") {
                val msgId = call.parameters["id"]?.toLong() ?: return@delete
                val msg = msgStub.getMessage(MessageId { id = msgId }).toRestMessageResponse() ?: return@delete
                msgStub.deleteMessage(MessageId { id = msgId })

                call.respond(HttpStatusCode.OK, "Message [${msg.content}] was successfully deleted")
            }

            patch("/edit") {
                val newMsg = call.receive<RestEditMessage>()
                val response = msgStub.editMessage(MessageEditRequest {
                    id = newMsg.id
                    content = newMsg.content
                }).toRestMessageResponse() ?: return@patch

                call.respond(HttpStatusCode.OK, response)
            }
        }

    }
}

private fun channelForTarget(host: String = "localhost", port: Int = 8080): ManagedChannel {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()
}
