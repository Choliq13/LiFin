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
    onPinVerified: () -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    val context = LocalContext.current
    val pinRepository = remember { PinRepository(context) }
    val lockManager = remember { AppLockManager.getInstance(context) }

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

    BackHandler(enabled = true) {
        (context as? Activity)?.finishAffinity()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bgawal),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.5f
        )

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
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masukkan PIN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF293E00),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Masukkan 6 digit PIN Anda",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(boxCount) { index ->
                            SinglePinBox(
                                value = pinDigits[index],
                                onValueChange = { newChar ->
                                    if (newChar.length <= 1 && newChar.all { it.isDigit() }) {
                                        pinDigits[index] = newChar
                                        if (newChar.isNotEmpty()) {
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

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    val canSubmit = pinValue().length == boxCount
                    Button(
                        onClick = { submitIfReady() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF738A45),
                            disabledContainerColor = Color(0xFF738A45).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
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
                                onNavigateToRegister()
                            }
                        )
                    }
                }
            }
        }
    }

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
private fun RowScope.SinglePinBox(
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
            .width(42.dp)
            .height(56.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                val isBackspace =
                    event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL
                if (isBackspace) {
                    onBackspace()
                    true
                } else false
            },
        singleLine = true,
        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF293E00)
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.5f),
            focusedContainerColor = container,
            unfocusedContainerColor = Color(0xFFF8F8F8),
            cursorColor = Color(0xFF738A45),
            focusedTextColor = Color(0xFF293E00),
            unfocusedTextColor = Color(0xFF293E00)
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
