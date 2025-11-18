package com.example.steptracker.model

data class StepTrackerUiState(
    val stepCount: Int = 0,
    val path: List<TrailPoint> = emptyList(),
    val distanceMeters: Float = 0f,
    val isLocationPermissionGranted: Boolean = false,
    val isActivityPermissionGranted: Boolean = false,
    val isTracking: Boolean = false,
    val stepSensorAvailable: Boolean = true,
    val statusMessage: String = ""
) {
    val isReady: Boolean
        get() = isLocationPermissionGranted && isActivityPermissionGranted
}
