@file:JvmName("Application")

package cdodi.com.gateway

import cdodi.com.gateway.plugins.configureAuth
import io.ktor.server.application.*
import cdodi.com.gateway.plugins.configureRouting
import cdodi.com.gateway.plugins.configureSerialization
import cdodi.com.gateway.plugins.configureSockets

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureAuth()
    configureSockets()
    configureRouting()
}
