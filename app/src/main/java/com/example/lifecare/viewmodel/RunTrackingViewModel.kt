package com.example.lifecare.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifecare.data.LiveRunState
import com.example.lifecare.data.RunActivity
import com.example.lifecare.repository.RunRepository
import com.example.lifecare.service.LocationTrackingService
import com.example.lifecare.utils.GPSUtils
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunTrackingViewModel(
    application: Application,
    private val runRepository: RunRepository
) : AndroidViewModel(application) {

    private var trackingService: LocationTrackingService? = null
    private var serviceBound = false

    private val _liveRunState = MutableStateFlow(LiveRunState())
    val liveRunState: StateFlow<LiveRunState> = _liveRunState.asStateFlow()

    private val _runHistory = MutableStateFlow<List<RunActivity>>(emptyList())
    val runHistory: StateFlow<List<RunActivity>> = _runHistory.asStateFlow()

    // Setup screen state
    private val _selectedActivityType = MutableStateFlow("Lari")
    val selectedActivityType: StateFlow<String> = _selectedActivityType.asStateFlow()

    private val _targetDistance = MutableStateFlow<Double?>(null)
    val targetDistance: StateFlow<Double?> = _targetDistance.asStateFlow()

    private val _targetDuration = MutableStateFlow<Long?>(null)
    val targetDuration: StateFlow<Long?> = _targetDuration.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("RunTrackingVM", "Service connected")
            val binder = service as? LocationTrackingService.LocalBinder
            trackingService = binder?.getService()
            serviceBound = true

            // Observe service state
            trackingService?.let { trackingServiceInstance ->
                Log.d("RunTrackingVM", "Starting to observe service state")
                viewModelScope.launch {
                    trackingServiceInstance.runState.collect { state ->
                        Log.d("RunTrackingVM", "Received state update: ${state.routePoints.size} points, distance: ${state.distance}")
                        _liveRunState.value = state
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("RunTrackingVM", "Service disconnected")
            trackingService = null
            serviceBound = false
        }
    }

    init {
        loadRunHistory()
    }

    private fun loadRunHistory() {
        viewModelScope.launch {
            runRepository.runs.collect { runs ->
                _runHistory.value = runs
            }
        }
    }

    // Setup screen actions
    fun setActivityType(type: String) {
        _selectedActivityType.value = type
    }

    fun setTargetDistance(distance: Double?) {
        _targetDistance.value = distance
    }

    fun setTargetDuration(duration: Long?) {
        _targetDuration.value = duration
    }

    // Tracking actions
    fun startTracking(context: Context) {
        val intent = Intent(context, LocationTrackingService::class.java)

        // Start service first
        context.startForegroundService(intent)

        // Bind to service
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Wait a bit for service to bind, then start tracking
        viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Give time for binding
            trackingService?.startTracking(
                activityType = _selectedActivityType.value,
                targetDistance = _targetDistance.value,
                targetDuration = _targetDuration.value
            )
        }
    }

    fun pauseTracking() {
        Log.d("RunTrackingVM", "Pause tracking requested, service bound: $serviceBound")
        trackingService?.pauseTracking() ?: Log.e("RunTrackingVM", "Service is null, cannot pause")
    }

    fun resumeTracking() {
        Log.d("RunTrackingVM", "Resume tracking requested, service bound: $serviceBound")
        trackingService?.resumeTracking() ?: Log.e("RunTrackingVM", "Service is null, cannot resume")
    }

    fun stopTracking(context: Context, saveRun: Boolean = true) {
        if (saveRun && _liveRunState.value.distance > 0) {
            val runActivity = RunActivity(
                id = System.currentTimeMillis().toString(),
                timestamp = System.currentTimeMillis(),
                activityType = _selectedActivityType.value,
                isGPSTracked = true,
                distance = _liveRunState.value.distance,
                duration = _liveRunState.value.duration,
                averagePace = _liveRunState.value.averagePace,
                averageSpeed = _liveRunState.value.averageSpeed,
                caloriesBurned = _liveRunState.value.calories,
                routePoints = _liveRunState.value.routePoints,
                elevationGain = _liveRunState.value.elevationGain,
                targetDistance = _targetDistance.value,
                targetDuration = _targetDuration.value
            )
            saveRunActivity(runActivity)
        }

        trackingService?.stopTracking()

        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }

        context.stopService(Intent(context, LocationTrackingService::class.java))

        // Reset state
        _liveRunState.value = LiveRunState()
        _targetDistance.value = null
        _targetDuration.value = null
    }

    fun saveRunActivity(run: RunActivity) {
        viewModelScope.launch {
            runRepository.saveRun(run)
        }
    }

    fun deleteRun(runId: String) {
        viewModelScope.launch {
            runRepository.deleteRun(runId)
        }
    }

    fun getRunById(runId: String): RunActivity? {
        return runRepository.getRunById(runId)
    }

    // Statistics
    fun getTotalDistance(): Double = runRepository.getTotalDistance()
    fun getTotalDuration(): Long = runRepository.getTotalDuration()
    fun getTotalCalories(): Int = runRepository.getTotalCalories()
    fun getAveragePace(): Double = runRepository.getAveragePace()
    fun getLongestRun(): RunActivity? = runRepository.getLongestRun()
    fun getFastestPace(): RunActivity? = runRepository.getFastestPace()

    // Helper functions
    fun formatDistance(distance: Double): String = GPSUtils.formatDistance(distance)
    fun formatDuration(seconds: Long): String = GPSUtils.formatDuration(seconds)
    fun formatPace(pace: Double): String = GPSUtils.formatPace(pace)
    fun formatSpeed(speed: Double): String = GPSUtils.formatSpeed(speed)

    override fun onCleared() {
        super.onCleared()
        if (serviceBound) {
            try {
                getApplication<Application>().unbindService(serviceConnection)
            } catch (e: Exception) {
                // Service already unbound
            }
        }
    }
}
