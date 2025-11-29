package com.example.lifin.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lifin.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSetPin: () -> Unit,
    onNavigateToPinVerify: () -> Unit,
    onNavigateToHome: () -> Unit,
    isLoggedIn: Boolean,
    isPinSet: Boolean
) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 detik splash

        // Navigation logic
        when {
            !isLoggedIn -> onNavigateToLogin()
            // Jika user sudah login tapi belum set PIN -> Set PIN terlebih dahulu
            isLoggedIn && !isPinSet -> onNavigateToSetPin()
            // Jika user sudah login dan PIN sudah diset -> minta verifikasi PIN sebelum masuk Home
            else -> onNavigateToPinVerify()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo LifIn+ (logolifin1.png - logo utama)
            Image(
                painter = painterResource(id = R.drawable.logolifin1),
                contentDescription = "LiFin Logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline (logolifin2.png - timbangan dalam kendali)
            Image(
                painter = painterResource(id = R.drawable.logolifin2),
                contentDescription = "LiFin Tagline",
                modifier = Modifier
                    .width(180.dp)
                    .height(40.dp)
            )
        }
    }
}
