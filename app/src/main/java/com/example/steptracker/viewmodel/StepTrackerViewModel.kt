package com.example.steptracker.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.location.TrailTracker
import com.example.steptracker.model.StepTrackerUiState
import com.example.steptracker.model.TrailMath
import com.example.steptracker.model.TrailPoint
import com.example.steptracker.sensor.StepCounterDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StepTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val stepCounterDataSource = StepCounterDataSource(application)
    private val trailTracker = TrailTracker(application)

    private val _state = MutableStateFlow(
        StepTrackerUiState(stepSensorAvailable = stepCounterDataSource.isSensorAvailable())
    )
    val state: StateFlow<StepTrackerUiState> = _state.asStateFlow()

    private var stepJob: Job? = null
    private var locationJob: Job? = null

    fun refreshPermissions() {
        val context = getApplication<Application>()
        val activityGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        updatePermissions(activityGranted, fineGranted)
    }

    fun updatePermissions(activityGranted: Boolean, locationGranted: Boolean) {
        _state.update {
            it.copy(
                isActivityPermissionGranted = activityGranted,
                isLocationPermissionGranted = locationGranted,
                statusMessage = when {
                    !activityGranted || !locationGranted -> "等待权限以开始追踪"
                    !it.stepSensorAvailable -> "设备不支持计步传感器"
                    else -> "追踪进行中"
                }
            )
        }
        if (activityGranted && locationGranted) {
            startTracking()
        } else {
            stopTracking()
        }
    }

    private fun startTracking() {
        if (stepJob == null) {
            stepJob = viewModelScope.launch {
                stepCounterDataSource.stepCountFlow().collect { steps ->
                    _state.update { current ->
                        current.copy(
                            stepCount = steps,
                            isTracking = current.isReady
                        )
                    }
                }
            }
        }

        if (locationJob == null) {
            locationJob = viewModelScope.launch {
                trailTracker.locationUpdates().collect { location ->
                    val newPoint = TrailPoint(location.latitude, location.longitude)
                    _state.update { current ->
                        val updatedPath = current.path + newPoint
                        current.copy(
                            path = updatedPath,
                            distanceMeters = TrailMath.totalDistance(updatedPath),
                            isTracking = current.isReady
                        )
                    }
                }
            }
        }
    }

    private fun stopTracking() {
        stepJob?.cancel()
        locationJob?.cancel()
        stepJob = null
        locationJob = null
        _state.update {
            it.copy(isTracking = false)
        }
    }

}
