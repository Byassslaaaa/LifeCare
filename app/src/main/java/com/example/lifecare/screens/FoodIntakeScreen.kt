package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.FoodIntake
import com.example.lifecare.data.HealthDataManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var foodList by remember { mutableStateOf(healthDataManager.getFoodIntakeList()) }
    val todayCalories = healthDataManager.getTodayTotalCaloriesIntake()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asupan Makanan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFFF9800)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Total Kalori Hari Ini",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$todayCalories kal",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            // History List
            Text(
                "Riwayat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(foodList) { food ->
                    FoodIntakeHistoryItem(food)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDialog) {
            AddFoodIntakeDialog(
                onDismiss = { showDialog = false },
                onSave = { name, calories, mealType, protein, carbs, fat ->
                    val food = FoodIntake(
                        foodName = name,
                        calories = calories,
                        mealType = mealType,
                        protein = protein,
                        carbs = carbs,
                        fat = fat
                    )
                    healthDataManager.saveFoodIntake(food)
                    foodList = healthDataManager.getFoodIntakeList()
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun FoodIntakeHistoryItem(food: FoodIntake) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        food.foodName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${food.mealType} • ${dateFormat.format(Date(food.timestamp))}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    "${food.calories} kal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            }

            if (food.protein != null || food.carbs != null || food.fat != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (food.protein != null) {
                        NutrientLabel("Protein", "${food.protein}g")
                    }
                    if (food.carbs != null) {
                        NutrientLabel("Karbo", "${food.carbs}g")
                    }
                    if (food.fat != null) {
                        NutrientLabel("Lemak", "${food.fat}g")
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientLabel(label: String, value: String) {
    Column {
        Text(
            label,
            fontSize = 10.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodIntakeDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, String, Double?, Double?, Double?) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var foodNameError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }
    var mealTypeError by remember { mutableStateOf<String?>(null) }
    var proteinError by remember { mutableStateOf<String?>(null) }
    var carbsError by remember { mutableStateOf<String?>(null) }
    var fatError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val mealTypes = listOf("Sarapan", "Makan Siang", "Makan Malam", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Asupan Makanan") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = {
                        if (it.length <= 50) {
                            foodName = it
                            foodNameError = null
                        }
                    },
                    label = { Text("Nama Makanan") },
                    placeholder = { Text("Contoh: Nasi Goreng") },
                    singleLine = true,
                    isError = foodNameError != null,
                    supportingText = {
                        if (foodNameError != null) {
                            Text(foodNameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = {},
                        label = { Text("Jenis Waktu Makan") },
                        readOnly = true,
                        isError = mealTypeError != null,
                        supportingText = {
                            if (mealTypeError != null) {
                                Text(mealTypeError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    mealType = type
                                    mealTypeError = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = calories,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 5)) {
                            calories = it
                            caloriesError = null
                        }
                    },
                    label = { Text("Kalori") },
                    placeholder = { Text("Contoh: 500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = caloriesError != null,
                    supportingText = {
                        if (caloriesError != null) {
                            Text(caloriesError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Nutrisi (Opsional)",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = protein,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            protein = it
                            proteinError = null
                        }
                    },
                    label = { Text("Protein (gram)") },
                    placeholder = { Text("Contoh: 25.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = proteinError != null,
                    supportingText = {
                        if (proteinError != null) {
                            Text(proteinError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = carbs,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            carbs = it
                            carbsError = null
                        }
                    },
                    label = { Text("Karbohidrat (gram)") },
                    placeholder = { Text("Contoh: 60.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = carbsError != null,
                    supportingText = {
                        if (carbsError != null) {
                            Text(carbsError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fat,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            fat = it
                            fatError = null
                        }
                    },
                    label = { Text("Lemak (gram)") },
                    placeholder = { Text("Contoh: 15.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = fatError != null,
                    supportingText = {
                        if (fatError != null) {
                            Text(fatError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false

                    // Validate food name
                    if (foodName.isEmpty()) {
                        foodNameError = "Nama makanan harus diisi"
                        hasError = true
                    } else if (foodName.length < 2) {
                        foodNameError = "Nama terlalu pendek"
                        hasError = true
                    }

                    // Validate meal type
                    if (mealType.isEmpty()) {
                        mealTypeError = "Pilih waktu makan"
                        hasError = true
                    }

                    // Validate calories
                    val cal = calories.toIntOrNull()
                    if (calories.isEmpty()) {
                        caloriesError = "Kalori harus diisi"
                        hasError = true
                    } else if (cal == null) {
                        caloriesError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (cal <= 0) {
                        caloriesError = "Kalori harus lebih dari 0"
                        hasError = true
                    } else if (cal > 10000) {
                        caloriesError = "Nilai terlalu besar"
                        hasError = true
                    }

                    // Validate protein if provided
                    val prot = if (protein.isNotEmpty()) {
                        val protValue = protein.toDoubleOrNull()
                        if (protValue == null) {
                            proteinError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (protValue < 0) {
                            proteinError = "Nilai tidak boleh negatif"
                            hasError = true
                            null
                        } else {
                            protValue
                        }
                    } else null

                    // Validate carbs if provided
                    val carb = if (carbs.isNotEmpty()) {
                        val carbValue = carbs.toDoubleOrNull()
                        if (carbValue == null) {
                            carbsError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (carbValue < 0) {
                            carbsError = "Nilai tidak boleh negatif"
                            hasError = true
                            null
                        } else {
                            carbValue
                        }
                    } else null

                    // Validate fat if provided
                    val f = if (fat.isNotEmpty()) {
                        val fatValue = fat.toDoubleOrNull()
                        if (fatValue == null) {
                            fatError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (fatValue < 0) {
                            fatError = "Nilai tidak boleh negatif"
                            hasError = true
                            null
                        } else {
                            fatValue
                        }
                    } else null

                    if (!hasError && cal != null) {
                        // Show calorie info
                        val message = when {
                            cal >= 800 -> "⚠️ Makanan berkalori tinggi ($cal kal). Perhatikan porsi!"
                            cal >= 500 -> "Makanan berkalori sedang ($cal kal)."
                            cal >= 200 -> "Makanan berkalori rendah ($cal kal)."
                            else -> "Snack ringan ($cal kal)."
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onSave(foodName, cal, mealType, prot, carb, f)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
