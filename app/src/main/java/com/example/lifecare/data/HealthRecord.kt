package com.example.lifecare.data

import java.util.Date

// Model untuk data user
data class UserData(
    val fullName: String,
    val email: String,
    val password: String,
    val age: String,
    val gender: String
)

// Model untuk data berat dan tinggi badan
data class BodyMetrics(
    val id: String = System.currentTimeMillis().toString(),
    val weight: Double, // dalam kg
    val height: Double, // dalam cm
    val bmi: Double = calculateBMI(weight, height),
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun calculateBMI(weight: Double, height: Double): Double {
            val heightInMeters = height / 100
            return if (heightInMeters > 0) weight / (heightInMeters * heightInMeters) else 0.0
        }
    }
}

// Model untuk tekanan darah
data class BloodPressure(
    val id: String = System.currentTimeMillis().toString(),
    val systolic: Int, // mmHg (atas)
    val diastolic: Int, // mmHg (bawah)
    val heartRate: Int?, // BPM (optional)
    val timestamp: Long = System.currentTimeMillis()
)

// Model untuk kadar gula darah
data class BloodSugar(
    val id: String = System.currentTimeMillis().toString(),
    val level: Double, // mg/dL
    val measurementType: String, // "Puasa", "Setelah Makan", "Random"
    val timestamp: Long = System.currentTimeMillis()
)

// Model untuk aktivitas fisik
data class PhysicalActivity(
    val id: String = System.currentTimeMillis().toString(),
    val activityType: String, // "Jalan", "Lari", "Bersepeda", dll.
    val duration: Int, // dalam menit
    val steps: Int?, // jumlah langkah (optional)
    val caloriesBurned: Int?, // kalori terbakar (optional)
    val timestamp: Long = System.currentTimeMillis()
)

// Model untuk asupan makanan
data class FoodIntake(
    val id: String = System.currentTimeMillis().toString(),
    val foodName: String,
    val calories: Int,
    val mealType: String, // "Sarapan", "Makan Siang", "Makan Malam", "Snack"
    val protein: Double?, // gram (optional)
    val carbs: Double?, // gram (optional)
    val fat: Double?, // gram (optional)
    val timestamp: Long = System.currentTimeMillis()
)
