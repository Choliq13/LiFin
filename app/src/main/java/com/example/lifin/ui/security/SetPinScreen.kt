package com.example.lifin.ui.security

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R
import com.example.lifin.data.repository.PinRepository
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

enum class SetPinStep {
    ENTER_PIN,
    CONFIRM_PIN
}

@Composable
fun SetPinScreen(
    onPinSet: () -> Unit
) {
    val context = LocalContext.current
    val pinRepository = remember { PinRepository(context) }
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(SetPinStep.ENTER_PIN) }
    val boxCount = 6

    val firstDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val confirmDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val firstFocus = remember { List(boxCount) { FocusRequester() } }
    val confirmFocus = remember { List(boxCount) { FocusRequester() } }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun firstPin(): String = firstDigits.joinToString("")
    fun confirmPin(): String = confirmDigits.joinToString("")

    fun clearFirst() {
        for (i in 0 until boxCount) firstDigits[i] = ""
        firstFocus[0].requestFocus()
    }
    fun clearConfirm() {
        for (i in 0 until boxCount) confirmDigits[i] = ""
        confirmFocus[0].requestFocus()
    }

    fun proceedOrError() {
        if (firstPin().length == boxCount) {
            step = SetPinStep.CONFIRM_PIN
            showError = false
            errorMessage = null
        } else {
            showError = true
            errorMessage = "PIN harus 6 digit"
        }
    }

    fun finishIfValid() {
        val f = firstPin()
        val c = confirmPin()
        if (c.length != boxCount) {
            showError = true
            errorMessage = "PIN harus 6 digit"
            return
        }
        if (f == c) {
            val result = pinRepository.setupPin(c)
            result.onSuccess { onPinSet() }
                .onFailure { error ->
                    errorMessage = error.message ?: "Gagal menyimpan PIN"
                    showError = true
                }
        } else {
            showError = true
            errorMessage = "PIN tidak cocok, coba lagi"
            scope.launch {
                kotlinx.coroutines.delay(1500)
                showError = false
                errorMessage = null
                step = SetPinStep.ENTER_PIN
                clearFirst()
                clearConfirm()
            }
        }
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
                .padding(horizontal = 32.dp),
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
                        text = if (step == SetPinStep.ENTER_PIN) "Buat PIN" else "Konfirmasi PIN",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF293E00),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (step == SetPinStep.ENTER_PIN) "Masukkan 6 digit PIN untuk keamanan" else "Konfirmasi PIN yang telah dibuat",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(boxCount) { index ->
                            val value =
                                if (step == SetPinStep.ENTER_PIN) firstDigits[index] else confirmDigits[index]
                            val requester =
                                if (step == SetPinStep.ENTER_PIN) firstFocus[index] else confirmFocus[index]

                            PinDigitBox(
                                value = value,
                                onValueChange = { ch ->
                                    if (ch.length <= 1 && ch.all { it.isDigit() }) {
                                        if (step == SetPinStep.ENTER_PIN) {
                                            firstDigits[index] = ch
                                            if (ch.isNotEmpty()) {
                                                if (index < boxCount - 1) {
                                                    firstFocus[index + 1].requestFocus()
                                                } else {
                                                    proceedOrError()
                                                }
                                            }
                                        } else {
                                            confirmDigits[index] = ch
                                            if (ch.isNotEmpty()) {
                                                if (index < boxCount - 1) {
                                                    confirmFocus[index + 1].requestFocus()
                                                } else {
                                                    finishIfValid()
                                                }
                                            }
                                        }
                                    }
                                },
                                onBackspace = {
                                    if (step == SetPinStep.ENTER_PIN) {
                                        if (firstDigits[index].isEmpty() && index > 0) {
                                            firstDigits[index - 1] = ""
                                            firstFocus[index - 1].requestFocus()
                                        } else if (firstDigits[index].isNotEmpty()) {
                                            firstDigits[index] = ""
                                        }
                                    } else {
                                        if (confirmDigits[index].isEmpty() && index > 0) {
                                            confirmDigits[index - 1] = ""
                                            confirmFocus[index - 1].requestFocus()
                                        } else if (confirmDigits[index].isNotEmpty()) {
                                            confirmDigits[index] = ""
                                        }
                                    }
                                },
                                focusRequester = requester,
                                isError = showError
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (showError && errorMessage != null) {
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

                    Button(
                        onClick = {
                            if (step == SetPinStep.ENTER_PIN) proceedOrError() else finishIfValid()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF738A45),
                            disabledContainerColor = Color(0xFF738A45).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = if (step == SetPinStep.ENTER_PIN)
                            firstPin().length == boxCount
                        else
                            confirmPin().length == boxCount
                    ) {
                        Text(
                            text = if (step == SetPinStep.ENTER_PIN) "Lanjutkan" else "Selesai",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (step == SetPinStep.CONFIRM_PIN) {
                        Text(
                            text = "Ubah PIN?",
                            color = Color(0xFF738A45),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                step = SetPinStep.ENTER_PIN
                                clearConfirm()
                                showError = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.PinDigitBox(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean
) {
    val borderColor = if (isError) Color.Red else Color(0xFF738A45)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(42.dp)
            .height(56.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { evt ->
                val isBackspace =
                    evt.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL
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
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF8F8F8),
            cursorColor = Color(0xFF738A45),
            focusedTextColor = Color(0xFF293E00),
            unfocusedTextColor = Color(0xFF293E00)
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {}),
        placeholder = {},
        isError = isError
    )
}
