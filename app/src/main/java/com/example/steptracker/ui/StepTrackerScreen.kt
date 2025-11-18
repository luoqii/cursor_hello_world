package com.example.steptracker.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptracker.model.StepTrackerUiState
import com.example.steptracker.model.TrailPoint
import com.example.steptracker.projection.PathProjector
import com.example.steptracker.ui.theme.StepTrackerTheme
import com.example.steptracker.viewmodel.StepTrackerViewModel
import java.util.Locale

@Composable
fun StepTrackerApp() {
    StepTrackerTheme {
        val viewModel: StepTrackerViewModel = viewModel()
        StepTrackerRoute(viewModel = viewModel)
    }
}

@Composable
fun StepTrackerRoute(viewModel: StepTrackerViewModel) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val activityGranted = result[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        viewModel.updatePermissions(
            activityGranted = activityGranted,
            locationGranted = fineGranted
        )
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissions()
    }

    StepTrackerScreen(
        state = uiState,
        onRequestPermissions = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    )
}

@Composable
fun StepTrackerScreen(
    state: StepTrackerUiState,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "实时运动追踪",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "步数", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = state.stepCount.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "距离", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = String.format(Locale.getDefault(), "%.1f 米", state.distanceMeters),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        PathPreviewCard(path = state.path)

        StatusSection(state = state, onRequestPermissions = onRequestPermissions)
    }
}

@Composable
private fun PathPreviewCard(path: List<TrailPoint>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        val colorScheme = MaterialTheme.colorScheme
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "轨迹", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorScheme.background)
            ) {
                val projected = PathProjector.project(path, size.width, size.height)
                if (projected.isNotEmpty()) {
                    val pathToDraw = Path().apply {
                        moveTo(projected.first().x, projected.first().y)
                        projected.drop(1).forEach { offset ->
                            lineTo(offset.x, offset.y)
                        }
                    }
                    drawPath(
                        path = pathToDraw,
                        color = colorScheme.primary,
                        style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    projected.firstOrNull()?.let {
                        drawCircle(
                            color = Color(0xFF2E7D32),
                            radius = 10f,
                            center = Offset(it.x, it.y)
                        )
                    }
                    projected.lastOrNull()?.let {
                        drawCircle(
                            color = Color(0xFFD32F2F),
                            radius = 10f,
                            center = Offset(it.x, it.y)
                        )
                    }
                }
            }
            if (path.size < 2) {
                Text(text = "轨迹将会在这里展示", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "最后位置: %.4f, %.4f",
                        path.last().latitude,
                        path.last().longitude
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StatusSection(
    state: StepTrackerUiState,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val statusText = when {
                !state.stepSensorAvailable -> "设备不支持计步功能"
                !state.isReady -> "请授予运动和定位权限"
                state.isTracking -> "实时追踪中"
                else -> "已暂停"
            }
            Text(text = statusText, style = MaterialTheme.typography.bodyLarge)
            if (!state.isReady) {
                Button(onClick = onRequestPermissions) {
                    Text(text = "授予权限")
                }
            }
        }
    }
}
