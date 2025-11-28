package com.example.lifecare.repository

import com.example.lifecare.data.RunActivity
import com.example.lifecare.data.RoutePoint
import com.example.lifecare.data.HealthDataManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RunRepository(private val healthDataManager: HealthDataManager) {
    private val gson = Gson()
    private val _runs = MutableStateFlow<List<RunActivity>>(emptyList())
    val runs: Flow<List<RunActivity>> = _runs.asStateFlow()

    companion object {
        private const val KEY_RUNS = "gps_runs"
    }

    init {
        loadRuns()
    }

    private fun loadRuns() {
        val json = healthDataManager.getData(KEY_RUNS)
        if (json != null) {
            val type = object : TypeToken<List<RunActivity>>() {}.type
            _runs.value = gson.fromJson(json, type)
        }
    }

    fun saveRun(run: RunActivity) {
        val currentRuns = _runs.value.toMutableList()

        // Check if run with same ID exists (update) or add new
        val existingIndex = currentRuns.indexOfFirst { it.id == run.id }
        if (existingIndex != -1) {
            currentRuns[existingIndex] = run
        } else {
            currentRuns.add(run)
        }

        // Sort by timestamp descending (newest first)
        currentRuns.sortByDescending { it.timestamp }

        _runs.value = currentRuns
        healthDataManager.saveData(KEY_RUNS, gson.toJson(currentRuns))
    }

    fun deleteRun(runId: String) {
        val currentRuns = _runs.value.toMutableList()
        currentRuns.removeAll { it.id == runId }
        _runs.value = currentRuns
        healthDataManager.saveData(KEY_RUNS, gson.toJson(currentRuns))
    }

    fun getRunById(runId: String): RunActivity? {
        return _runs.value.find { it.id == runId }
    }

    fun getRunsByDateRange(startTimestamp: Long, endTimestamp: Long): List<RunActivity> {
        return _runs.value.filter { it.timestamp in startTimestamp..endTimestamp }
    }

    fun getTotalDistance(): Double {
        return _runs.value.sumOf { it.distance }
    }

    fun getTotalDuration(): Long {
        return _runs.value.sumOf { it.duration }
    }

    fun getTotalCalories(): Int {
        return _runs.value.sumOf { it.caloriesBurned }
    }

    fun getAveragePace(): Double {
        val gpsRuns = _runs.value.filter { it.isGPSTracked && it.distance > 0 }
        return if (gpsRuns.isNotEmpty()) {
            gpsRuns.map { it.averagePace }.average()
        } else {
            0.0
        }
    }

    fun getLongestRun(): RunActivity? {
        return _runs.value.filter { it.isGPSTracked }.maxByOrNull { it.distance }
    }

    fun getFastestPace(): RunActivity? {
        return _runs.value.filter { it.isGPSTracked && it.distance > 0 }.minByOrNull { it.averagePace }
    }
}
