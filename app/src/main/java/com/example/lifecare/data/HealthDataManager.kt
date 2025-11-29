package com.example.lifecare.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class HealthDataManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "health_data_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    // Keys untuk SharedPreferences
    companion object {
        private const val TAG = "HealthDataManager"
        private const val KEY_BODY_METRICS = "body_metrics"
        private const val KEY_BLOOD_PRESSURE = "blood_pressure"
        private const val KEY_BLOOD_SUGAR = "blood_sugar"
        private const val KEY_PHYSICAL_ACTIVITY = "physical_activity"
        private const val KEY_FOOD_INTAKE = "food_intake"
        private const val KEY_USER_PIN = "user_pin"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in" // Menyimpan status login persistent
        private const val KEY_PROFILE_PHOTO_URI = "profile_photo_uri" // URI foto profil
    }

    // ============ User Management ============
    fun saveUserData(fullName: String, email: String, password: String, age: String, gender: String) {
        val userData = UserData(fullName, email, password, age, gender)
        val json = gson.toJson(userData)
        sharedPreferences.edit().putString(KEY_USER_DATA, json).apply()
    }

    fun getUserData(): UserData? {
        val json = sharedPreferences.getString(KEY_USER_DATA, null) ?: return null
        return gson.fromJson(json, UserData::class.java)
    }

    fun verifyLogin(email: String, password: String): Boolean {
        val userData = getUserData() ?: return false
        return userData.email == email && userData.password == password
    }

    fun isUserRegistered(): Boolean {
        return getUserData() != null
    }

    // ============ PIN Management ============
    fun saveUserPIN(pin: String) {
        sharedPreferences.edit().putString(KEY_USER_PIN, pin).apply()
    }

    fun getUserPIN(): String? {
        return sharedPreferences.getString(KEY_USER_PIN, null)
    }

    fun verifyPIN(pin: String): Boolean {
        return pin == getUserPIN()
    }

    fun changePIN(oldPin: String, newPin: String): Boolean {
        if (verifyPIN(oldPin)) {
            saveUserPIN(newPin)
            return true
        }
        return false
    }

    fun isPINSet(): Boolean {
        return getUserPIN() != null
    }

    /**
     * Clear/delete PIN (untuk forgot PIN flow)
     */
    fun clearPIN() {
        sharedPreferences.edit().remove(KEY_USER_PIN).apply()
    }

    // ============ Login State Management ============
    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ============ Profile Photo Management ============
    fun saveProfilePhotoUri(uri: String) {
        sharedPreferences.edit().putString(KEY_PROFILE_PHOTO_URI, uri).apply()
    }

    fun getProfilePhotoUri(): String? {
        return sharedPreferences.getString(KEY_PROFILE_PHOTO_URI, null)
    }

    fun clearProfilePhoto() {
        sharedPreferences.edit().remove(KEY_PROFILE_PHOTO_URI).apply()
    }

    // ============ Body Metrics ============
    fun saveBodyMetrics(metrics: BodyMetrics) {
        val list = getBodyMetricsList().toMutableList()
        list.add(0, metrics) // Add to beginning
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_BODY_METRICS, json).apply()
    }

    fun getBodyMetricsList(): List<BodyMetrics> {
        return try {
            val json = sharedPreferences.getString(KEY_BODY_METRICS, null) ?: return emptyList()
            val type = object : TypeToken<List<BodyMetrics>>() {}.type
            val data: List<BodyMetrics>? = gson.fromJson(json, type)
            data ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error in body metrics data. Clearing corrupt data.", e)
            clearBodyMetricsData()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading body metrics data", e)
            emptyList()
        }
    }

    private fun clearBodyMetricsData() {
        sharedPreferences.edit().remove(KEY_BODY_METRICS).apply()
    }

    fun getLatestBodyMetrics(): BodyMetrics? {
        return getBodyMetricsList().firstOrNull()
    }

    // ============ Blood Pressure ============
    fun saveBloodPressure(bp: BloodPressure) {
        val list = getBloodPressureList().toMutableList()
        list.add(0, bp)
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_BLOOD_PRESSURE, json).apply()
    }

    fun getBloodPressureList(): List<BloodPressure> {
        return try {
            val json = sharedPreferences.getString(KEY_BLOOD_PRESSURE, null) ?: return emptyList()
            val type = object : TypeToken<List<BloodPressure>>() {}.type
            val data: List<BloodPressure>? = gson.fromJson(json, type)
            data ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error in blood pressure data. Clearing corrupt data.", e)
            clearBloodPressureData()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading blood pressure data", e)
            emptyList()
        }
    }

    private fun clearBloodPressureData() {
        sharedPreferences.edit().remove(KEY_BLOOD_PRESSURE).apply()
    }

    fun getLatestBloodPressure(): BloodPressure? {
        return getBloodPressureList().firstOrNull()
    }

    // ============ Blood Sugar ============
    fun saveBloodSugar(bs: BloodSugar) {
        val list = getBloodSugarList().toMutableList()
        list.add(0, bs)
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_BLOOD_SUGAR, json).apply()
    }

    fun getBloodSugarList(): List<BloodSugar> {
        return try {
            val json = sharedPreferences.getString(KEY_BLOOD_SUGAR, null) ?: return emptyList()
            val type = object : TypeToken<List<BloodSugar>>() {}.type
            val data: List<BloodSugar>? = gson.fromJson(json, type)
            data ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error in blood sugar data. Clearing corrupt data.", e)
            clearBloodSugarData()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading blood sugar data", e)
            emptyList()
        }
    }

    private fun clearBloodSugarData() {
        sharedPreferences.edit().remove(KEY_BLOOD_SUGAR).apply()
    }

    fun getLatestBloodSugar(): BloodSugar? {
        return getBloodSugarList().firstOrNull()
    }

    // ============ Physical Activity ============
    fun savePhysicalActivity(activity: PhysicalActivity) {
        val list = getPhysicalActivityList().toMutableList()
        list.add(0, activity)
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_PHYSICAL_ACTIVITY, json).apply()
    }

    fun getPhysicalActivityList(): List<PhysicalActivity> {
        return try {
            val json = sharedPreferences.getString(KEY_PHYSICAL_ACTIVITY, null) ?: return emptyList()
            val type = object : TypeToken<List<PhysicalActivity>>() {}.type
            val data: List<PhysicalActivity>? = gson.fromJson(json, type)
            data ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error in physical activity data. Clearing corrupt data.", e)
            clearPhysicalActivityData()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading physical activity data", e)
            emptyList()
        }
    }

    private fun clearPhysicalActivityData() {
        sharedPreferences.edit().remove(KEY_PHYSICAL_ACTIVITY).apply()
    }

    // ============ Food Intake ============
    fun saveFoodIntake(food: FoodIntake) {
        val list = getFoodIntakeList().toMutableList()
        list.add(0, food)
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_FOOD_INTAKE, json).apply()
    }

    fun getFoodIntakeList(): List<FoodIntake> {
        return try {
            val json = sharedPreferences.getString(KEY_FOOD_INTAKE, null) ?: return emptyList()
            val type = object : TypeToken<List<FoodIntake>>() {}.type
            val data: List<FoodIntake>? = gson.fromJson(json, type)
            data ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON syntax error in food intake data. Clearing corrupt data.", e)
            clearFoodIntakeData()
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error reading food intake data", e)
            emptyList()
        }
    }

    private fun clearFoodIntakeData() {
        sharedPreferences.edit().remove(KEY_FOOD_INTAKE).apply()
    }

    // ============ Statistics ============
    fun getTodayTotalCaloriesIntake(): Int {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % 86400000) // Start of current day
        return getFoodIntakeList()
            .filter { it.timestamp >= startOfDay }
            .sumOf { it.calories }
    }

    fun getTodayTotalSteps(): Int {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % 86400000)
        return getPhysicalActivityList()
            .filter { it.timestamp >= startOfDay }
            .sumOf { it.steps ?: 0 }
    }

    fun getTodayTotalExerciseMinutes(): Int {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % 86400000)
        return getPhysicalActivityList()
            .filter { it.timestamp >= startOfDay }
            .sumOf { it.duration }
    }

    // ============ Generic Data Access (for GPS Tracking) ============
    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    // ============ Clear All Data ============
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}
