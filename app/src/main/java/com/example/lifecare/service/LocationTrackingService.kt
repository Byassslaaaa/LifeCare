package com.example.lifecare.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.lifecare.MainActivity
import com.example.lifecare.R
import com.example.lifecare.data.LiveRunState
import com.example.lifecare.data.RoutePoint
import com.example.lifecare.utils.GPSUtils
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val _runState = MutableStateFlow(LiveRunState())
    val runState: StateFlow<LiveRunState> = _runState.asStateFlow()

    private val binder = LocalBinder()

    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var lastPauseTimestamp: Long = 0
    private var lastUpdateTime: Long = 0

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val UPDATE_INTERVAL = 2000L // 2 seconds
        private const val FASTEST_INTERVAL = 1000L // 1 second
        private const val MAX_WAIT_TIME = 5000L // 5 seconds
        private const val ACCURACY_THRESHOLD = 25f // meters
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    @SuppressLint("MissingPermission")
    fun startTracking(
        activityType: String = "Lari",
        targetDistance: Double? = null,
        targetDuration: Long? = null
    ) {
        startTime = System.currentTimeMillis()
        lastUpdateTime = startTime
        pausedTime = 0

        _runState.value = LiveRunState(
            isTracking = true,
            isPaused = false,
            activityType = activityType,
            targetDistance = targetDistance,
            targetDuration = targetDuration,
            startTime = startTime
        )

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            setMaxUpdateDelayMillis(MAX_WAIT_TIME)
            setWaitForAccurateLocation(true)
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pauseTracking() {
        if (!_runState.value.isTracking || _runState.value.isPaused) return

        lastPauseTimestamp = System.currentTimeMillis()
        _runState.value = _runState.value.copy(isPaused = true)

        updateNotification()
    }

    fun resumeTracking() {
        if (!_runState.value.isTracking || !_runState.value.isPaused) return

        val pauseDuration = System.currentTimeMillis() - lastPauseTimestamp
        pausedTime += pauseDuration

        _runState.value = _runState.value.copy(
            isPaused = false,
            pausedDuration = pausedTime
        )

        updateNotification()
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            if (_runState.value.isPaused) return

            val location = locationResult.lastLocation ?: return
            updateLocation(location)
        }
    }

    private fun updateLocation(location: Location) {
        // Check accuracy
        if (location.accuracy > ACCURACY_THRESHOLD) {
            return // Skip inaccurate points
        }

        val currentState = _runState.value
        val currentTime = System.currentTimeMillis()

        val newPoint = RoutePoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            timestamp = currentTime,
            accuracy = location.accuracy
        )

        val updatedPoints = currentState.routePoints + newPoint

        // Calculate distance
        val totalDistance = GPSUtils.calculateTotalDistance(updatedPoints)

        // Calculate duration (excluding paused time)
        val activeDuration = ((currentTime - startTime - pausedTime) / 1000).coerceAtLeast(0)

        // Calculate speeds and pace
        val averageSpeed = GPSUtils.calculateSpeed(totalDistance, activeDuration)
        val averagePace = GPSUtils.calculatePace(totalDistance, activeDuration)

        // Calculate current pace (last 30 seconds)
        val currentPace = GPSUtils.calculateCurrentPace(updatedPoints, currentTime)
        val currentSpeed = if (currentTime - lastUpdateTime > 0) {
            val timeDiff = (currentTime - lastUpdateTime) / 1000.0
            val distanceDiff = if (currentState.lastLocation != null) {
                GPSUtils.calculateDistance(
                    currentState.lastLocation.latitude,
                    currentState.lastLocation.longitude,
                    newPoint.latitude,
                    newPoint.longitude
                )
            } else 0.0
            GPSUtils.calculateSpeed(distanceDiff, timeDiff.toLong().coerceAtLeast(1))
        } else {
            averageSpeed
        }

        // Calculate calories
        val calories = GPSUtils.calculateCalories(
            totalDistance,
            activeDuration,
            currentState.activityType
        )

        // Calculate elevation
        val (elevationGain, elevationLoss) = GPSUtils.calculateElevation(updatedPoints)

        _runState.value = currentState.copy(
            distance = totalDistance,
            duration = activeDuration,
            currentPace = currentPace,
            averagePace = averagePace,
            currentSpeed = currentSpeed,
            averageSpeed = averageSpeed,
            calories = calories,
            elevationGain = elevationGain,
            elevationLoss = elevationLoss,
            routePoints = updatedPoints,
            lastLocation = newPoint
        )

        lastUpdateTime = currentTime

        // Update notification with current stats
        updateNotification()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_location_tracking),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_location_tracking_desc)
            setShowBadge(false)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val state = _runState.value

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = if (state.isPaused) {
            "GPS Tracking - DIJEDA"
        } else {
            "GPS Tracking - ${state.activityType}"
        }

        val contentText = buildString {
            append("${GPSUtils.formatDistance(state.distance)} • ")
            append(GPSUtils.formatDuration(state.duration))
            if (state.averagePace > 0) {
                append(" • ${GPSUtils.formatPace(state.averagePace)}")
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
