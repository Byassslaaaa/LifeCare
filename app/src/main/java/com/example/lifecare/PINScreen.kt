package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager

@Composable
fun PINScreen(
    healthDataManager: HealthDataManager,
    onPINVerified: () -> Unit,
    forceCreateMode: Boolean = false
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val existingPIN = healthDataManager.getUserPIN()
    val isSettingPIN = forceCreateMode || existingPIN == null

    // Keyboard otomatis
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = if (isSettingPIN) "Buat PIN Anda" else "Masukkan PIN Anda",
            fontSize = 14.sp,
            color = Color(0xFF5A5A5A)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // PIN BOX
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        ) {
            repeat(6) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(4.dp)
                        .background(Color(0xFFEDEDED), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = if (index < pin.length) "●" else "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Invisible TextField
        TextField(
            value = pin,
            onValueChange = {
                if (it.length <= 6 && it.all(Char::isDigit)) {
                    pin = it
                }
            },
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester),

            // keyboard numeric
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),

            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (pin.length != 6) {
                    Toast.makeText(context, "PIN harus 6 digit", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (isSettingPIN) {
                    healthDataManager.saveUserPIN(pin)
                    Toast.makeText(context, "PIN berhasil dibuat", Toast.LENGTH_SHORT).show()
                    onPINVerified()
                } else {
                    if (healthDataManager.verifyPIN(pin)) {
                        onPINVerified()
                    } else {
                        Toast.makeText(context, "PIN salah!", Toast.LENGTH_SHORT).show()
                        pin = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF33A1E0))
        ) {
            Text(
                text = if (isSettingPIN) "Buat PIN" else "Masuk",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(240.dp))

        Text(
            text = "Forgot PIN",
            color = Color(0xFFB7D800),
            fontSize = 12.sp
        )
    }
}