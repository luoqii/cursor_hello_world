package com.example.steptracker.model

object TrailMath {
    fun totalDistance(points: List<TrailPoint>): Float {
        if (points.size < 2) return 0f
        var total = 0f
        for (index in 1 until points.size) {
            total += points[index - 1].distanceTo(points[index])
        }
        return total
    }
}
