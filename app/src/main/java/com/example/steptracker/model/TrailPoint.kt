package com.example.steptracker.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

data class TrailPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

fun TrailPoint.distanceTo(other: TrailPoint): Float {
    val earthRadiusMeters = 6_371_000.0
    val dLat = (other.latitude - latitude).toRadians()
    val dLon = (other.longitude - longitude).toRadians()
    val lat1 = latitude.toRadians()
    val lat2 = other.latitude.toRadians()

    val a = sin(dLat / 2).pow2() + cos(lat1) * cos(lat2) * sin(dLon / 2).pow2()
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (earthRadiusMeters * c).toFloat()
}

private fun Double.pow2(): Double = this * this
private fun Double.toRadians(): Double = this / 180.0 * PI
