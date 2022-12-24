@file:JvmName("Main")
package cdodi.com.data

fun main(args: Array<String>) {
    DatabaseFactory.init()
    val server = DataServer(8080)
    server.start()
    server.blockUntilShutdown()
}
