package com.example

import io.grpc.examples.routeguide.Feature
import io.grpc.examples.routeguide.Point
import io.grpc.examples.routeguide.Rectangle
import io.grpc.examples.routeguide.RouteGuideGrpcKt
import io.grpc.examples.routeguide.RouteNote
import io.grpc.examples.routeguide.RouteSummary
import kotlinx.coroutines.flow.Flow

class RouteGuideService: RouteGuideGrpcKt.RouteGuideCoroutineImplBase() {

    override suspend fun getFeature(request: Point): Feature {
        return super.getFeature(request)
    }

    override fun listFeatures(request: Rectangle): Flow<Feature> {
        return super.listFeatures(request)
    }

    override suspend fun recordRoute(requests: Flow<Point>): RouteSummary {
        return super.recordRoute(requests)
    }

    override fun routeChat(requests: Flow<RouteNote>): Flow<RouteNote> {
        return super.routeChat(requests)
    }

}