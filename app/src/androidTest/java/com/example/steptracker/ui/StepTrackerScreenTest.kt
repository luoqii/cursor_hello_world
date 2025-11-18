package com.example.steptracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.steptracker.model.StepTrackerUiState
import com.example.steptracker.ui.theme.StepTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StepTrackerScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsPermissionButtonWhenPermissionsMissing() {
        composeRule.setContent {
            StepTrackerTheme {
                StepTrackerScreen(
                    state = StepTrackerUiState(
                        isActivityPermissionGranted = false,
                        isLocationPermissionGranted = false,
                        stepSensorAvailable = true
                    ),
                    onRequestPermissions = {}
                )
            }
        }

        composeRule.onNodeWithText("授予权限").assertIsDisplayed()
    }
}
