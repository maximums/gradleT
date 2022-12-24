package cdodi.com.gateway.plugins

import cdodi.com.gateway.Email
import cdodi.com.gateway.dto.RestUserRequest
import cdodi.com.gateway.toRestUserResponse
import cdodi.com.gateway.toUserRequest
import com.cdodi.data.*
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
            val resp = stub.getAllUsers(AllUsersRequest.getDefaultInstance()).usersList.map(User::toRestUserResponse)
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
                val resp = msgStub.getMessage(MessageId.newBuilder().setId(msgId).build()).toString()
                call.respond(HttpStatusCode.OK, resp)
            }
            get("/all") {
                val resp = msgStub.getAllMessages(AllMessageRequest.newBuilder().setOffset(0).build()).toString()
                call.respond(HttpStatusCode.OK, resp)
            }
            post("/add") {  }
            delete("/del") {  }
            patch("/edit") {  }
        }

    }
}

private fun channelForTarget(host: String = "localhost", port: Int = 8080): ManagedChannel {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()
}
