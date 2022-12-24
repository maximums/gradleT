package cdodi.com.gateway.plugins

import com.cdodi.data.AllUsersRequest
import com.cdodi.data.DataServiceGrpcKt
import com.cdodi.data.UserEmail
import com.cdodi.data.UserRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    val channel = ManagedChannelBuilder.forAddress("data-service", 8080).usePlaintext().build()
    val stub = DataServiceGrpcKt.DataServiceCoroutineStub(channel)

    routing {
        get("/{email}") {
            val email = call.parameters["email"] ?: "bad"
            val resp = stub.getUser(UserEmail.newBuilder().setEmail(email).build())
            call.respond(HttpStatusCode.OK, resp.toString())
        }

        get("/all") {
            val resp = stub.getAllUsers(AllUsersRequest.getDefaultInstance()).toString()
            call.respond(HttpStatusCode.OK, resp)
        }

        post("/some") {
            val response = stub.createUser(UserRequest.newBuilder()
                .setName("name")
                .setEmail("some@gamil.com")
                .setPassword("letmein")
                .setDateOfBirth(166666)
                .setAvatarId(1)
                .setHobbiesIds("hobbies")
                .build()
            )
            call.respond(HttpStatusCode.OK, response.toString())
        }

        post("/test/{id}") {
            val id = call.parameters["id"] ?: "without id today"
            call.respond("Ok <-> $id")
        }
    }
}

private fun channelForTarget(host: String = "localhost", port: Int = 8080): ManagedChannel {
    return ManagedChannelBuilder
        .forAddress(host, port)
        .usePlaintext()
        .build()
}
