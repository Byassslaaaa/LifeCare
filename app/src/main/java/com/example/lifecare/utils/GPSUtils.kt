package com.example.lifecare.utils

import com.example.lifecare.data.RoutePoint
import kotlin.math.*

object GPSUtils {

    /**
     * Calculate distance between two GPS coordinates using Haversine formula
     * Returns distance in kilometers
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * Calculate total distance from a list of route points
     */
    fun calculateTotalDistance(routePoints: List<RoutePoint>): Double {
        if (routePoints.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 1 until routePoints.size) {
            val prev = routePoints[i - 1]
            val current = routePoints[i]
            totalDistance += calculateDistance(
                prev.latitude, prev.longitude,
                current.latitude, current.longitude
            )
        }

        return totalDistance
    }

    /**
     * Calculate pace in minutes per kilometer
     */
    fun calculatePace(distance: Double, durationSeconds: Long): Double {
        if (distance <= 0) return 0.0
        return (durationSeconds / 60.0) / distance
    }

    /**
     * Calculate speed in kilometers per hour
     */
    fun calculateSpeed(distance: Double, durationSeconds: Long): Double {
        if (durationSeconds <= 0) return 0.0
        return (distance / durationSeconds) * 3600.0
    }

    /**
     * Format pace to "MM:SS/km" format
     */
    fun formatPace(pace: Double): String {
        if (pace <= 0 || pace.isInfinite() || pace.isNaN()) return "--:--/km"

        val minutes = pace.toInt()
        val seconds = ((pace - minutes) * 60).toInt()

        return "%d:%02d/km".format(minutes, seconds)
    }

    /**
     * Format speed to "X.X km/h" format
     */
    fun formatSpeed(speed: Double): String {
        if (speed.isInfinite() || speed.isNaN()) return "0.0 km/h"
        return "%.1f km/h".format(speed)
    }

    /**
     * Format distance to readable string
     */
    fun formatDistance(distance: Double): String {
        return when {
            distance < 0.01 -> "0.00 km"
            distance < 1.0 -> "%.2f km".format(distance)
            distance < 10.0 -> "%.2f km".format(distance)
            else -> "%.1f km".format(distance)
        }
    }

    /**
     * Format duration in seconds to "HH:MM:SS" or "MM:SS"
     */
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, secs)
        } else {
            "%d:%02d".format(minutes, secs)
        }
    }

    /**
     * Calculate calories burned based on distance, duration, and activity type
     * Using MET (Metabolic Equivalent) values
     */
    fun calculateCalories(
        distance: Double,
        durationSeconds: Long,
        activityType: String,
        weightKg: Double = 70.0 // Default weight
    ): Int {
        val hours = durationSeconds / 3600.0

        val met = when (activityType) {
            "Lari" -> {
                val speed = calculateSpeed(distance, durationSeconds)
                when {
                    speed < 8.0 -> 8.0   // Slow jogging
                    speed < 10.0 -> 9.0  // Moderate running
                    speed < 12.0 -> 11.0 // Fast running
                    else -> 12.5         // Very fast running
                }
            }
            "Jalan Kaki" -> {
                val speed = calculateSpeed(distance, durationSeconds)
                when {
                    speed < 4.0 -> 3.0   // Slow walk
                    speed < 5.5 -> 3.5   // Moderate walk
                    else -> 4.5          // Brisk walk
                }
            }
            "Bersepeda" -> {
                val speed = calculateSpeed(distance, durationSeconds)
                when {
                    speed < 16.0 -> 6.0  // Leisure cycling
                    speed < 20.0 -> 8.0  // Moderate cycling
                    speed < 25.0 -> 10.0 // Fast cycling
                    else -> 12.0         // Very fast cycling
                }
            }
            else -> 6.0 // Default MET value
        }

        // Calories = MET × weight (kg) × time (hours)
        return (met * weightKg * hours).toInt()
    }

    /**
     * Calculate elevation gain and loss from route points
     * Returns Pair(gain, loss) in meters
     */
    fun calculateElevation(routePoints: List<RoutePoint>): Pair<Double, Double> {
        if (routePoints.size < 2) return Pair(0.0, 0.0)

        var gain = 0.0
        var loss = 0.0

        for (i in 1 until routePoints.size) {
            val elevationChange = routePoints[i].altitude - routePoints[i - 1].altitude
            if (elevationChange > 0) {
                gain += elevationChange
            } else {
                loss += abs(elevationChange)
            }
        }

        return Pair(gain, loss)
    }

    /**
     * Check if a location is accurate enough for tracking
     */
    fun isLocationAccurate(accuracy: Float, threshold: Float = 25f): Boolean {
        return accuracy in 0f..threshold
    }

    /**
     * Filter route points by accuracy
     */
    fun filterAccuratePoints(routePoints: List<RoutePoint>, accuracyThreshold: Float = 25f): List<RoutePoint> {
        return routePoints.filter { isLocationAccurate(it.accuracy, accuracyThreshold) }
    }

    /**
     * Calculate current pace from recent points (last 30 seconds)
     */
    fun calculateCurrentPace(routePoints: List<RoutePoint>, currentTimestamp: Long): Double {
        if (routePoints.size < 2) return 0.0

        val recentPoints = routePoints.filter { currentTimestamp - it.timestamp <= 30000 }
        if (recentPoints.size < 2) return 0.0

        val distance = calculateTotalDistance(recentPoints)
        val duration = (recentPoints.last().timestamp - recentPoints.first().timestamp) / 1000

        return calculatePace(distance, duration)
    }
}
