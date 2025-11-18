package com.example.steptracker.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrailMathTest {

    @Test
    fun `totalDistance sums consecutive segments`() {
        val points = listOf(
            TrailPoint(0.0, 0.0),
            TrailPoint(0.0, 0.001),
            TrailPoint(0.001, 0.001)
        )

        val distance = TrailMath.totalDistance(points)

        // Each ~0.001 degree is roughly 111 meters at the equator
        assertTrue(distance > 0f)
    }

    @Test
    fun `totalDistance returns zero when insufficient points`() {
        assertEquals(0f, TrailMath.totalDistance(emptyList()))
        assertEquals(0f, TrailMath.totalDistance(listOf(TrailPoint(0.0, 0.0))))
    }
}
