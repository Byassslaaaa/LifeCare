package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
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
                title = {
                    Text(
                        "Tambah Asupan Makanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = HealthColors.NeonGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = HealthColors.NeonGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
                colors = CardDefaults.cardColors(containerColor = HealthColors.NeonGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Total Kalori Hari ini",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$todayCalories Kal",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Text(
                "Riwayat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(foodList) { food ->
                    FoodIntakeHistoryItem(food)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showDialog) {
            AddFoodIntakeDialog(
                onDismiss = { showDialog = false },
                onSave = { name, calories, mealType, protein, carbs, fat ->
                    val food = FoodIntake(foodName = name, calories = calories, mealType = mealType, protein = protein, carbs = carbs, fat = fat)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        food.foodName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HealthColors.NeonGreen
                    )
                    Text(
                        "${food.mealType} • ${dateFormat.format(Date(food.timestamp))}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Text(
                    "${food.calories} Kal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HealthColors.NeonGreen
                )
            }

            if (food.protein != null || food.carbs != null || food.fat != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (food.protein != null) NutrientLabel("Protein", "${food.protein}g")
                    if (food.carbs != null) NutrientLabel("Karbo", "${food.carbs}g")
                    if (food.fat != null) NutrientLabel("Lemak", "${food.fat}g")
                }
            }
        }
    }
}

@Composable
fun NutrientLabel(name: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            "$name: $value",
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodIntakeDialog(onDismiss: () -> Unit, onSave: (String, Int, String, Double?, Double?, Double?) -> Unit) {
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
    val context = LocalContext.current

    val mealTypes = listOf("Sarapan", "Makan Siang", "Makan Malam", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Asupan Makanan", style = HealthTypography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = foodName,
                    onValueChange = { foodName = it; foodNameError = null },
                    placeholder = { Text("Nama Makanan") },
                    singleLine = true,
                    isError = foodNameError != null,
                    supportingText = { if (foodNameError != null) Text(foodNameError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50.dp)
                )

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        value = mealType,
                        onValueChange = {},
                        placeholder = { Text("Waktu Makan") },
                        readOnly = true,
                        isError = mealTypeError != null,
                        supportingText = { if (mealTypeError != null) Text(mealTypeError!!, color = HealthColors.Error) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { mealType = type; mealTypeError = null; expanded = false })
                        }
                    }
                }

                TextField(
                    value = calories,
                    onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 5)) { calories = it; caloriesError = null } },
                    placeholder = { Text("Kalori") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = caloriesError != null,
                    supportingText = { if (caloriesError != null) Text(caloriesError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = protein,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) protein = it },
                        placeholder = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                    TextField(
                        value = carbs,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) carbs = it },
                        placeholder = { Text("Karbo (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                    TextField(
                        value = fat,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) fat = it },
                        placeholder = { Text("Lemak (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (foodName.isEmpty()) { foodNameError = "Nama makanan harus diisi"; hasError = true }
                    if (mealType.isEmpty()) { mealTypeError = "Pilih waktu makan"; hasError = true }

                    val cal = calories.toIntOrNull()
                    if (calories.isEmpty()) { caloriesError = "Kalori harus diisi"; hasError = true }
                    else if (cal == null) { caloriesError = "Masukkan angka yang valid"; hasError = true }
                    else if (cal <= 0) { caloriesError = "Kalori harus lebih dari 0"; hasError = true }

                    val prot = if (protein.isNotEmpty()) protein.toDoubleOrNull() else null
                    val carb = if (carbs.isNotEmpty()) carbs.toDoubleOrNull() else null
                    val ft = if (fat.isNotEmpty()) fat.toDoubleOrNull() else null

                    if (!hasError && cal != null) {
                        val message = when {
                            cal > 800 -> "Makanan tinggi kalori. Perhatikan asupan harian!"
                            cal > 500 -> "Asupan tersimpan. Jaga keseimbangan nutrisi!"
                            else -> "Asupan tersimpan!"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onSave(foodName, cal, mealType, prot, carb, ft)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HealthColors.NeonGreen),
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(50.dp)
            ) { Text("Simpan", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) } }
    )
}
