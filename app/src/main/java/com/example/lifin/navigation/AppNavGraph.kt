package com.example.lifin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifin.ui.auth.LoginScreen
import com.example.lifin.ui.auth.RegisterScreen
import com.example.lifin.ui.calendar.CalendarScreen
import com.example.lifin.ui.home.HomeScreen
import com.example.lifin.ui.profile.ProfileScreen
import com.example.lifin.ui.profile.EditProfileScreen
import com.example.lifin.ui.security.ChangePasswordScreen
import com.example.lifin.ui.security.PinVerifyScreen
import java.text.SimpleDateFormat
import java.util.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object SetPin : Screen("set_pin")
    object PinVerify : Screen("pin_verify")
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object ChangePassword : Screen("change_password")
    object ForgotPassword : Screen("forgot_password")
    object EditProfile : Screen("edit_profile")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authRepository: com.example.lifin.data.repository.AuthRepository,
    pinRepository: com.example.lifin.data.repository.PinRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            // Check auth status dynamically each time splash is shown
            val isLoggedIn = authRepository.isLoggedIn()
            val isPinSet = pinRepository.isPinSet()

            com.example.lifin.ui.splash.SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSetPin = {
                    navController.navigate(Screen.SetPin.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToPinVerify = {
                    navController.navigate(Screen.PinVerify.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn,
                isPinSet = isPinSet
            )
        }

        // Login
        composable(Screen.Login.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
            com.example.lifin.ui.auth.LoginScreen(
                onLoginSuccess = {
                    // mark login date
                    val iso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    prefs.setLastLoginDateIso(iso)
                    val isPinSet = pinRepository.isPinSet()
                    if (isPinSet) {
                        navController.navigate(Screen.PinVerify.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.SetPin.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // Forgot Password
        composable(Screen.ForgotPassword.route) {
            com.example.lifin.ui.auth.ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    // Back to Login after reset
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                }
            )
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Setelah register berhasil, kembali ke login screen
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Set PIN (setelah login pertama kali)
        composable(Screen.SetPin.route) {
            com.example.lifin.ui.security.SetPinScreen(
                onPinSet = {
                    // Setelah PIN dibuat, wajib verifikasi saat masuk berikutnya, tapi sekarang boleh ke Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SetPin.route) { inclusive = true }
                    }
                }
            )
        }

        // PIN Verify (saat buka app lagi / setelah login jika sudah punya PIN)
        composable(Screen.PinVerify.route) {
            PinVerifyScreen(
                onPinVerified = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinVerify.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
            HomeScreen(
                onNavigateToHome = { },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onHealthNoteAdded = {
                    // store note date for calendar
                    val iso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    prefs.addHealthNoteDate(iso)
                }
            )
        }

        // Calendar
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToCalendar = { }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                authRepository = authRepository,
                pinRepository = pinRepository,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateToProfile = { },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        // Edit Profile
        composable(Screen.EditProfile.route) {
            com.example.lifin.ui.profile.EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onNavigateToChangePin = { navController.navigate(Screen.SetPin.route) }
            )
        }

        // Change Password
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onPasswordChanged = {
                    // After changing password, go back to profile
                    navController.popBackStack()
                }
            )
        }
    }
}
