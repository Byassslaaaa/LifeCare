package com.example.lifecare.data

/**
 * Data class for GPS-tracked running/cycling activities
 */
data class RunActivity(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val activityType: String, // "Lari", "Jalan Kaki", "Bersepeda"
    val isGPSTracked: Boolean = false,
    val distance: Double, // kilometers
    val duration: Long, // seconds
    val averagePace: Double, // min/km
    val averageSpeed: Double, // km/h
    val caloriesBurned: Int,
    val routePoints: List<RoutePoint> = emptyList(),
    val elevationGain: Double = 0.0,
    val elevationLoss: Double = 0.0,
    val notes: String = "",
    val targetDistance: Double? = null,
    val targetDuration: Long? = null
)

/**
 * Individual GPS coordinate point in a route
 */
data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val accuracy: Float = 0f
)

/**
 * Live state during an active run tracking session
 */
data class LiveRunState(
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val distance: Double = 0.0,
    val duration: Long = 0,
    val currentPace: Double = 0.0,
    val averagePace: Double = 0.0,
    val currentSpeed: Double = 0.0,
    val averageSpeed: Double = 0.0,
    val calories: Int = 0,
    val elevationGain: Double = 0.0,
    val elevationLoss: Double = 0.0,
    val routePoints: List<RoutePoint> = emptyList(),
    val lastLocation: RoutePoint? = null,
    val activityType: String = "Lari",
    val targetDistance: Double? = null,
    val targetDuration: Long? = null,
    val startTime: Long = 0,
    val pausedDuration: Long = 0
)
