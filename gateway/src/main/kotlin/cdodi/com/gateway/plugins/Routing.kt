package cdodi.com.gateway.plugins

import cdodi.com.gateway.*
import cdodi.com.gateway.controllers.messageRoutes
import cdodi.com.gateway.controllers.userRoutes
import cdodi.com.gateway.dto.AuthData
import cdodi.com.gateway.dto.RestUserRequest
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cdodi.data.*
import io.grpc.ManagedChannelBuilder
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

private const val ONE_HOUR: Long = 3_600_000L

fun Application.configureRouting() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val channel = ManagedChannelBuilder.forAddress("data-service", 8080).usePlaintext().build()
    val msgStub = MessageServiceGrpcKt.MessageServiceCoroutineStub(channel)
    val stub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

    routing {
        post("/login") {
            val authData = call.receive<AuthData>()

            val user = stub.getUser(Email { email = authData.email }).toRestUserResponse()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "User with email: '${authData.email}' doesn't exist")
                return@post
            }

            if (user.password != authData.password) {
                call.respond(HttpStatusCode.Unauthorized, "Incorrect credentials")
                return@post
            }

            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("email", authData.email)
                .withExpiresAt(Date(System.currentTimeMillis() + ONE_HOUR))
                .sign(Algorithm.HMAC256(secret))

            call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $email! Token is expired at $expiresAt ms.")
            }

            post("/add") {
                val newUser: UserRequest = call.receive<RestUserRequest>().toUserRequest()
                val response = stub.createUser(newUser).toRestUserResponse()
                if (response == null) {
                    call.respond(HttpStatusCode.BadRequest, "Unable to create new user")
                    return@post
                }

                call.respond(HttpStatusCode.OK, response)
            }

            createRouteFromPath("/user").userRoutes(msgStub, stub)
            createRouteFromPath("/msg").messageRoutes(msgStub, stub)
        }
    }
}
