package com.example.lifin.ui.security

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordChanged: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPassword by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ubah Password",
                        fontSize = 20.sp,  // Diperbesar dari 18sp ke 20sp
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF293E00)  // Warna hijau tua
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF293E00)  // Icon juga hijau tua
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Background
            Image(
                painter = painterResource(id = R.drawable.bgawal),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5).copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PasswordField(
                        label = "Password Saat Ini",
                        value = currentPassword,
                        onChange = { currentPassword = it }
                    )
                    PasswordField(
                        label = "Password Baru",
                        value = newPassword,
                        onChange = { newPassword = it }
                    )
                    PasswordField(
                        label = "Konfirmasi Password Baru",
                        value = confirmPassword,
                        onChange = { confirmPassword = it }
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    if (showForgotPassword) {
                        TextButton(
                            onClick = onNavigateToForgotPassword,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Lupa Password?",
                                color = Color(0xFF738A45),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isSubmitting) return@Button
                            isSubmitting = true
                            errorMessage = null
                            when {
                                currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                                    errorMessage = "Semua field harus diisi"
                                }
                                newPassword.length < 6 -> {
                                    errorMessage = "Password baru minimal 6 karakter"
                                }
                                newPassword != confirmPassword -> {
                                    errorMessage = "Konfirmasi password tidak cocok"
                                }
                                else -> {
                                    // Integrasi dengan Supabase AuthRepository
                                    scope.launch {
                                        val result = com.example.lifin.data.repository.AuthRepository(context).changePassword(currentPassword, newPassword)
                                        if (result.isSuccess) {
                                            snackbarHostState.showSnackbar("Password berhasil diubah")
                                            showForgotPassword = false
                                            onPasswordChanged()
                                        } else {
                                            val error = result.exceptionOrNull()?.message ?: "Gagal mengubah password"
                                            errorMessage = error
                                            // Jika password lama salah, tampilkan tombol lupa password
                                            if (error.contains("salah", ignoreCase = true) || error.contains("incorrect", ignoreCase = true) || error.contains("current", ignoreCase = true)) {
                                                showForgotPassword = true
                                            }
                                        }
                                        isSubmitting = false
                                    }
                                    return@Button
                                }
                            }
                            if (errorMessage != null) {
                                scope.launch { snackbarHostState.showSnackbar(errorMessage!!) }
                                isSubmitting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),  // Diperbesar dari 48dp ke 56dp
                        shape = RoundedCornerShape(50.dp),  // Lebih rounded
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF738A45))
                    ) {
                        Text(
                            "Simpan Perubahan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp  // Font size button text diperbesar
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(label: String, value: String, onChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF293E00)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) androidx.compose.material.icons.Icons.Filled.Visibility else androidx.compose.material.icons.Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0xFF738A45)
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF738A45),
                unfocusedBorderColor = Color(0xFF738A45).copy(alpha = 0.5f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF5F9F3)
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF293E00)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
