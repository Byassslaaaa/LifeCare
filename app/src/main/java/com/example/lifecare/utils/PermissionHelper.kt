package com.example.lifecare.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object PermissionHelper {
    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation && coarseLocation
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required for Android 9 and below
        }
    }

    fun getRequiredLocationPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun getBackgroundLocationPermission(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            null
        }
    }
}

@Composable
fun rememberLocationPermissionState(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
): LocationPermissionState {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(PermissionHelper.hasLocationPermission(context))
    }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        hasPermission = granted

        if (granted) {
            onPermissionGranted()
        } else {
            shouldShowRationale = true
            onPermissionDenied()
        }
    }

    return remember {
        LocationPermissionState(
            hasPermission = hasPermission,
            shouldShowRationale = shouldShowRationale,
            launcher = launcher,
            onDismissRationale = { shouldShowRationale = false }
        )
    }
}

data class LocationPermissionState(
    val hasPermission: Boolean,
    val shouldShowRationale: Boolean,
    val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    val onDismissRationale: () -> Unit
) {
    fun requestPermission() {
        launcher.launch(PermissionHelper.getRequiredLocationPermissions())
    }
}
