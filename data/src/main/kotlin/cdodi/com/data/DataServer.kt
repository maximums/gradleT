package cdodi.com.data

import cdodi.com.data.services.MessageService
import cdodi.com.data.services.UserService
import io.grpc.ServerBuilder

class DataServer(private val port: Int) {
    private val server = ServerBuilder
        .forPort(port)
        .addService(UserService())
        .addService(MessageService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@DataServer.stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}