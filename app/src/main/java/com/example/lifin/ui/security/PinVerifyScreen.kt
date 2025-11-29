package com.example.lifin.ui.security

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R
import com.example.lifin.data.repository.PinRepository
import com.example.lifin.security.AppLockManager
import kotlinx.coroutines.launch

@Composable
fun PinVerifyScreen(
    onPinVerified: () -> Unit
) {
    val context = LocalContext.current
    val pinRepository = remember { PinRepository(context) }
    val lockManager = remember { AppLockManager.getInstance(context) }

    // State
    val boxCount = 6
    val pinDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(boxCount) { FocusRequester() } }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var attemptCount by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    fun pinValue(): String = pinDigits.joinToString("")

    fun clearWithDelay() {
        scope.launch {
            kotlinx.coroutines.delay(1200)
            for (i in 0 until boxCount) pinDigits[i] = ""
            focusRequesters[0].requestFocus()
            isError = false
            errorMessage = null
        }
    }

    fun submitIfReady() {
        val pin = pinValue()
        if (pin.length == boxCount) {
            val isValid = pinRepository.verifyPin(pin)
            if (isValid) {
                lockManager.unlock()
                onPinVerified()
            } else {
                attemptCount++
                isError = true
                errorMessage = "PIN salah, coba lagi"
                clearWithDelay()
            }
        } else {
            isError = true
            errorMessage = "PIN harus $boxCount digit"
        }
    }

    // Disable back navigation to prevent bypass; back should close the app
    BackHandler(enabled = true) {
        (context as? Activity)?.finishAffinity()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bgawal),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.5f
        )

        // Content Card centered
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5).copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title aligned start like screenshot
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Masukan PIN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PIN boxes row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(boxCount) { index ->
                            SinglePinBox(
                                value = pinDigits[index],
                                onValueChange = { newChar ->
                                    if (newChar.length <= 1 && newChar.all { it.isDigit() }) {
                                        pinDigits[index] = newChar
                                        if (newChar.isNotEmpty()) {
                                            // move to next focus
                                            if (index < boxCount - 1) {
                                                focusRequesters[index + 1].requestFocus()
                                            } else {
                                                submitIfReady()
                                            }
                                        }
                                    }
                                },
                                onBackspace = {
                                    if (pinDigits[index].isEmpty() && index > 0) {
                                        pinDigits[index - 1] = ""
                                        focusRequesters[index - 1].requestFocus()
                                    } else if (pinDigits[index].isNotEmpty()) {
                                        pinDigits[index] = ""
                                    }
                                },
                                focusRequester = focusRequesters[index],
                                isError = isError
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Masuk Button
                    val canSubmit = pinValue().length == boxCount
                    Button(
                        onClick = { submitIfReady() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF738A45)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = canSubmit
                    ) {
                        Text(
                            "Masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // "Belum punya akun? Daftar" text
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Daftar",
                            color = Color(0xFF738A45),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                // Navigation handled by parent if needed
                            }
                        )
                    }
                }
            }
        }
    }

    // Lock after 5 failed attempts
    if (attemptCount >= 5) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Terlalu Banyak Percobaan") },
            text = { Text("Anda telah mencoba terlalu banyak kali. Silakan coba lagi nanti.") },
            confirmButton = {
                TextButton(onClick = { attemptCount = 0 }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun SinglePinBox(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean
) {
    val borderColor = if (isError) Color.Red else Color(0xFF738A45)
    val container = Color.White

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(48.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                val isBackspace = event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL
                if (isBackspace) { onBackspace(); true } else false
            },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.6f),
            focusedContainerColor = container,
            unfocusedContainerColor = container,
            cursorColor = Color(0xFF738A45),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { }),
        placeholder = { },
        isError = isError
    )
}
