package com.example.lifin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.example.lifin.navigation.AppNavGraph
import com.example.lifin.navigation.Screen
import com.example.lifin.security.AppLockManager
import com.example.lifin.ui.theme.LiFinTheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- 1. MODEL DATA (Struktur Tabel) ---
@Serializable
data class WeightLog(
    val id: Long? = null,  // Nullable untuk handle auto-increment
    val weight: Double,
    @SerialName("created_at") val createdAt: String? = null  // Nullable untuk handle timestamp
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LiFinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Initialize repositories with fully qualified names to avoid import issues
                    val authRepository: com.example.lifin.data.repository.AuthRepository = remember {
                        com.example.lifin.data.repository.AuthRepository(this)
                    }
                    val pinRepository: com.example.lifin.data.repository.PinRepository = remember {
                        com.example.lifin.data.repository.PinRepository(this)
                    }
                    val lockManager = remember { AppLockManager.getInstance(this) }

                    // Observe app moving to foreground to enforce PIN when needed
                    DisposableEffect(Unit) {
                        val lifecycle = ProcessLifecycleOwner.get().lifecycle
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                lockManager.evaluateLockOnForeground()
                                if (lockManager.isLocked) {
                                    // Navigate to PIN verify if not already there
                                    val currentRoute = navController.currentDestination?.route
                                    if (currentRoute != Screen.PinVerify.route &&
                                        currentRoute != Screen.Login.route &&
                                        currentRoute != Screen.Register.route &&
                                        currentRoute != Screen.SetPin.route &&
                                        currentRoute != Screen.Splash.route
                                    ) {
                                        navController.navigate(Screen.PinVerify.route) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        }
                        lifecycle.addObserver(observer)
                        onDispose { lifecycle.removeObserver(observer) }
                    }

                    AppNavGraph(
                        navController = navController,
                        authRepository = authRepository,
                        pinRepository = pinRepository
                    )
                }
            }
        }
    }
}

// HomeScreen has been moved to ui/home/HomeScreen.kt
