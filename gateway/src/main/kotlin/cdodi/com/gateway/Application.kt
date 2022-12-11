@file:JvmName("Application")

package cdodi.com.gateway

import io.ktor.server.application.*
import cdodi.com.gateway.plugins.configureRouting

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureRouting()
}
