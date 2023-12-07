package com.example

import io.grpc.ManagedChannel
import io.grpc.examples.helloworld.GreeterGrpcKt
import io.grpc.examples.helloworld.helloRequest
import java.io.Closeable
import java.util.concurrent.TimeUnit

class HelloWorldClient(
    private val managedChannel: ManagedChannel
) : Closeable {

    private val stub: GreeterGrpcKt.GreeterCoroutineStub = GreeterGrpcKt.GreeterCoroutineStub(managedChannel)

    suspend fun greet(name: String) {
        val request = helloRequest {
            this.name = name
        }
        val response = stub.sayHello(request)
        println("Received: ${response.message}")
        val againResponse = stub.sayHelloAgain(request)
        println("Received: ${againResponse.message}")
    }

    override fun close() {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

}