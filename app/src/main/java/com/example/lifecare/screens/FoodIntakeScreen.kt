package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                title = { Text("Asupan Makanan", style = HealthTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.Food,
                    titleContentColor = HealthColors.TextOnPrimary,
                    navigationIconContentColor = HealthColors.TextOnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = HealthColors.Food,
                contentColor = HealthColors.TextOnPrimary
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
            FeaturedCard(
                modifier = Modifier.fillMaxWidth().padding(HealthSpacing.screenPadding),
                backgroundColor = HealthColors.Food,
                contentColor = HealthColors.TextOnPrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(HealthSpacing.iconSizeLarge), tint = HealthColors.TextOnPrimary)
                    Spacer(modifier = Modifier.width(HealthSpacing.medium))
                    Column {
                        Text("Total Kalori Hari Ini", style = HealthTypography.bodySmall, color = HealthColors.TextOnPrimary.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                        Text("$todayCalories kal", style = HealthTypography.displaySmall, fontWeight = FontWeight.Bold, color = HealthColors.TextOnPrimary)
                    }
                }
            }

            Text("Riwayat", style = HealthTypography.headlineSmall, modifier = Modifier.padding(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            ) {
                items(foodList) { food ->
                    FoodIntakeHistoryItem(food)
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
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

    HealthCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(food.foodName, style = HealthTypography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${food.mealType} • ${dateFormat.format(Date(food.timestamp))}", style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
            }
            Text("${food.calories} kal", style = HealthTypography.headlineSmall, fontWeight = FontWeight.Bold, color = HealthColors.Food)
        }

        if (food.protein != null || food.carbs != null || food.fat != null) {
            Spacer(modifier = Modifier.height(HealthSpacing.small))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small)) {
                if (food.protein != null) NutrientLabel("Protein", "${food.protein}g")
                if (food.carbs != null) NutrientLabel("Karbo", "${food.carbs}g")
                if (food.fat != null) NutrientLabel("Lemak", "${food.fat}g")
            }
        }
    }
}

@Composable
fun NutrientLabel(name: String, value: String) {
    Surface(color = HealthColors.FoodLight, shape = MaterialTheme.shapes.small) {
        Text("$name: $value", style = HealthTypography.labelSmall, modifier = Modifier.padding(horizontal = HealthSpacing.small, vertical = HealthSpacing.xxSmall), color = HealthColors.Food)
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
            Column(verticalArrangement = Arrangement.spacedBy(HealthSpacing.small)) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it; foodNameError = null },
                    label = { Text("Nama Makanan") },
                    placeholder = { Text("Contoh: Nasi Goreng") },
                    singleLine = true,
                    isError = foodNameError != null,
                    supportingText = { if (foodNameError != null) Text(foodNameError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
                )

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = mealType, onValueChange = {}, label = { Text("Waktu Makan") }, readOnly = true,
                        isError = mealTypeError != null,
                        supportingText = { if (mealTypeError != null) Text(mealTypeError!!, color = HealthColors.Error) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { mealType = type; mealTypeError = null; expanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 5)) { calories = it; caloriesError = null } },
                    label = { Text("Kalori") },
                    placeholder = { Text("Contoh: 450") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = caloriesError != null,
                    supportingText = { if (caloriesError != null) Text(caloriesError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HealthSpacing.xSmall)) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) protein = it },
                        label = { Text("Protein (g)") },
                        placeholder = { Text("15") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
                    )
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) carbs = it },
                        label = { Text("Karbo (g)") },
                        placeholder = { Text("60") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) fat = it },
                        label = { Text("Lemak (g)") },
                        placeholder = { Text("10") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Food, focusedLabelColor = HealthColors.Food)
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
                colors = ButtonDefaults.buttonColors(containerColor = HealthColors.Food)
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = HealthColors.TextSecondary) } }
    )
}
