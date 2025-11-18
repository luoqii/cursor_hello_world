package com.example.steptracker.projection

import androidx.compose.ui.geometry.Offset
import com.example.steptracker.model.TrailPoint
import kotlin.math.max

object PathProjector {

    fun project(
        points: List<TrailPoint>,
        width: Float,
        height: Float,
        padding: Float = 32f
    ): List<Offset> {
        if (points.size < 2 || width <= 0f || height <= 0f) return emptyList()
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLng = points.minOf { it.longitude }
        val maxLng = points.maxOf { it.longitude }

        val latRange = max(maxLat - minLat, 0.0001)
        val lngRange = max(maxLng - minLng, 0.0001)

        val usableWidth = (width - padding * 2).coerceAtLeast(1f)
        val usableHeight = (height - padding * 2).coerceAtLeast(1f)

        return points.map { point ->
            val x = ((point.longitude - minLng) / lngRange).toFloat()
            val y = ((maxLat - point.latitude) / latRange).toFloat()
            Offset(
                x = padding + x * usableWidth,
                y = padding + y * usableHeight
            )
        }
    }
}
