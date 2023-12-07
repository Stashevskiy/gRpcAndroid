package com.example

import io.grpc.ManagedChannel
import io.grpc.examples.routeguide.Feature
import io.grpc.examples.routeguide.Point
import io.grpc.examples.routeguide.RouteGuideGrpcKt
import io.grpc.examples.routeguide.RouteNote
import io.grpc.examples.routeguide.point
import io.grpc.examples.routeguide.rectangle
import io.grpc.examples.routeguide.routeNote
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class RouteGuideClient(
    private val channel: ManagedChannel
) : Closeable {

    private val random = Random(314159)

    private val stub = RouteGuideGrpcKt.RouteGuideCoroutineStub(channel)

    // Simple RPC
    suspend fun getFeature(latitude: Int, longitude: Int) {
        println("*** GetFeature: lat=$latitude lon=$longitude")

        val request = point {
            this.latitude = latitude
            this.longitude = longitude
        }
        val feature = stub.getFeature(request)
        println("*** Feature: $feature")
    }

    // Server-side streaming RPC
    suspend fun listFeatures(
        lowLatitude: Int,
        lowLongitude: Int,
        hiLatitude: Int,
        hiLongitude: Int
    ): List<Feature> {
        println("*** ListFeatures: lowLat=$lowLatitude lowLon=$lowLongitude hiLat=$hiLatitude liLon=$hiLongitude")

        val features = mutableListOf<Feature>()
        val request = rectangle {
            this.lo = point {
                this.latitude = lowLatitude
                this.longitude = lowLongitude
            }
            this.hi = point {
                this.latitude = hiLatitude
                this.longitude = hiLongitude
            }
        }
        val listFeatures = stub.listFeatures(request)
        var i = 1
        listFeatures.collect { feature ->
            println("Result #${i++}: $feature")
            features.add(feature)
        }
        return features
    }

    // Client-side streaming RPC
    suspend fun recordRoute(points: Flow<Point>) {
        println("*** RecordRoute")
        val summary = stub.recordRoute(points)
        println("Finished trip with ${summary.pointCount} points.")
        println("Passed ${summary.featureCount} features.")
        println("Travelled ${summary.distance} meters.")
        val duration = summary.elapsedTime.seconds
        println("It took $duration seconds.")
    }

    fun generateRoutePoints(features: List<Feature>, numPoints: Int): Flow<Point> = flow {
        for (i in 1..numPoints) {
            val feature = features.random(random)
            println("Visiting point ${feature.location}")
            emit(feature.location)
            delay(random.nextLong(500L, 1500L))
        }
    }

    // Bidirectional streaming RPC
    suspend fun routeChat() {
        println("*** RouteChat")
        val requests = generateOutgoingNotes()
        val gotNotes = stub.routeChat(requests)
        gotNotes.collect { note ->
            println("Got message \"${note.message}\" at ${note.location}")
        }
        println("Finished RouteChat")
    }

    private fun generateOutgoingNotes(): Flow<RouteNote> = flow {
        val notes = listOf(
            routeNote {
                message = "First message"
                location = point {
                    this.latitude = 0
                    this.longitude = 0
                }
            },
            routeNote {
                message = "Second message"
                location = point {
                    this.latitude = 0
                    this.longitude = 0
                }
            },
            routeNote {
                message = "Third message"
                location = point {
                    this.latitude = 10000000
                    this.longitude = 0
                }
            },
            routeNote {
                message = "Fourth message"
                location = point {
                    this.latitude = 10000000
                    this.longitude = 10000000
                }
            },
            routeNote {
                message = "Last message"
                location = point {
                    this.latitude = 0
                    this.longitude = 0
                }
            }
        )
        for (note in notes) {
            println("Sending message \"${note.message}\" at ${note.location}")
            emit(note)
            delay(500)
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

}