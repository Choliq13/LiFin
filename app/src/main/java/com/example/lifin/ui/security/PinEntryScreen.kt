package com.example.lifin.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions

@Composable
fun PinEntryScreen(
    title: String = "Masukan PIN",
    subtitle: String = "Masukkan 6 digit PIN Anda",
    onPinComplete: (String) -> Unit,
    isError: Boolean = false,
    onForgotPin: (() -> Unit)? = null
) {
    val boxCount = 6
    val digits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(boxCount) { FocusRequester() } }
    var localError by remember { mutableStateOf(isError) }

    LaunchedEffect(isError) { localError = isError }

    fun currentPin(): String = digits.joinToString("")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFFB0B0B0)
            )
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(boxCount) { index ->
                    val value = digits[index]
                    OutlinedTextField(
                        value = value,
                        onValueChange = { ch ->
                            if (ch.length <= 1 && ch.all { it.isDigit() }) {
                                digits[index] = ch
                                if (ch.isNotEmpty()) {
                                    if (index < boxCount - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        if (currentPin().length == boxCount) onPinComplete(currentPin())
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent { e ->
                                val isBackspace = e.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL
                                if (isBackspace) {
                                    if (digits[index].isEmpty() && index > 0) {
                                        digits[index - 1] = ""
                                        focusRequesters[index - 1].requestFocus()
                                    } else if (digits[index].isNotEmpty()) {
                                        digits[index] = ""
                                    }
                                    true
                                } else false
                            },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (localError) Color.Red else Color(0xFF4CAF50),
                            unfocusedBorderColor = if (localError) Color.Red else Color(0xFF3D3D3D),
                            focusedContainerColor = Color(0xFF2C2C2C),
                            unfocusedContainerColor = Color(0xFF2C2C2C),
                            cursorColor = Color(0xFF4CAF50),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {}),
                        placeholder = {},
                        isError = localError
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (localError) {
                Text(
                    text = "PIN salah, coba lagi",
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
            }

            // Backspace mass clear & optional forgot PIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    // Clear all digits
                    for (i in 0 until boxCount) digits[i] = ""
                    focusRequesters[0].requestFocus()
                    localError = false
                }) {
                    Text("Hapus", color = Color.White, fontSize = 14.sp)
                }
                if (onForgotPin != null) {
                    TextButton(onClick = onForgotPin) {
                        Text("Lupa PIN?", color = Color(0xFF4CAF50), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
