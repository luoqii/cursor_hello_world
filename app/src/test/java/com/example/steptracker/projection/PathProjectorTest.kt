package com.example.steptracker.projection

import com.example.steptracker.model.TrailPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PathProjectorTest {

    @Test
    fun `project returns empty when less than two points`() {
        val emptyProjection = PathProjector.project(emptyList(), 200f, 200f)
        assertTrue(emptyProjection.isEmpty())
    }

    @Test
    fun `project normalizes coordinates within canvas`() {
        val points = listOf(
            TrailPoint(10.0, 10.0, timestamp = 0),
            TrailPoint(10.5, 10.5, timestamp = 1),
            TrailPoint(11.0, 11.0, timestamp = 2)
        )

        val result = PathProjector.project(points, width = 400f, height = 400f, padding = 0f)

        assertEquals(points.size, result.size)
        assertTrue(result.first().x < result.last().x)
        assertTrue(result.first().y > result.last().y)
        assertTrue(result.all { it.x in 0f..400f && it.y in 0f..400f })
    }
}
