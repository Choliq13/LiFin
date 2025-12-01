package com.example.lifin.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R
import com.example.lifin.data.repository.AuthRepository
import com.example.lifin.data.repository.PinRepository
import com.example.lifin.ui.components.BottomNavigationBar
import com.example.lifin.ui.components.BottomNavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authRepository: AuthRepository,
    pinRepository: PinRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }

    // Colors
    val primaryColor = Color(0xFF293E00)
    val backgroundColor = Color(0xFFF0F9E9)

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Image(
            painter = painterResource(id = R.drawable.bglanding),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.TopCenter)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profil", fontWeight = FontWeight.Bold, color = primaryColor) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    selected = BottomNavItem.Profile,
                    onHome = onNavigateToHome,
                    onCalendar = onNavigateToCalendar,
                    onProfile = onNavigateToProfile,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHost) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    ProfileHeader(prefs, primaryColor)
                    Spacer(modifier = Modifier.height(24.dp))
                    ProfileMenuGroup(
                        prefs = prefs,
                        scope = scope,
                        snackbarHost = snackbarHost,
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToChangePassword = onNavigateToChangePassword,
                        onLogoutClick = { showLogoutDialog = true },
                        primaryColor = primaryColor
                    )
                }
            }

            if (showLogoutDialog) {
                LogoutDialog(
                    isProcessing = loggingOut,
                    onDismiss = { if (!loggingOut) showLogoutDialog = false },
                    onConfirm = {
                        loggingOut = true
                        scope.launch {
                            val result = authRepository.logout()
                            loggingOut = false
                            showLogoutDialog = false
                            if (result.isSuccess) {
                                snackbarHost.showSnackbar("Logout berhasil")
                                onLogoutSuccess()
                            } else {
                                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                                snackbarHost.showSnackbar("Logout gagal: $error")
                            }
                        }
                    }
                )
            }

            if (loggingOut) {
                LoadingOverlay()
            }
        }
    }
}

@Composable
private fun ProfileHeader(prefs: com.example.lifin.data.local.EncryptedPreferences, primaryColor: Color) {
    val fullName = prefs.getProfileName().ifBlank { "Pengguna Baru" }
    val avatarIndex = prefs.getProfileAvatarIndex()
    val avatarColors = remember {
        listOf(
            Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFBBDEFB), Color(0xFFC8E6C9),
            Color(0xFFFFF9C4), Color(0xFFD1C4E9), Color(0xFFFFE0B2)
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (avatarIndex >= 0) avatarColors[avatarIndex.coerceIn(0, avatarColors.lastIndex)]
                        else Color.LightGray
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Face,
                    contentDescription = "Avatar",
                    tint = Color(0xFF37474F),
                    modifier = Modifier.size(60.dp)
                )
            }
            Text(fullName, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = primaryColor)
        }
    }
}

@Composable
private fun ProfileMenuGroup(
    prefs: com.example.lifin.data.local.EncryptedPreferences,
    scope: CoroutineScope,
    snackbarHost: SnackbarHostState,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit,
    primaryColor: Color
) {
    var notificationEnabled by remember { mutableStateOf(prefs.isNotificationEnabled()) }
    var notifHour by remember { mutableStateOf(prefs.getNotificationHour()) }
    var notifMinute by remember { mutableStateOf(prefs.getNotificationMinute()) }
    val context = LocalContext.current

    // Single Card with all menu items
    Card(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F9F3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Edit,
                text = "Edit Profile",
                onClick = onNavigateToEditProfile,
                primaryColor = primaryColor
            )

            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                text = "Notifikasi",
                onClick = {},
                primaryColor = primaryColor,
                trailingContent = {
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { checked ->
                            notificationEnabled = checked
                            prefs.setNotificationEnabled(checked)
                            if (checked) {
                                scheduleDailyNotification(context, notifHour, notifMinute, scope, snackbarHost)
                            } else {
                                com.example.lifin.notification.NotificationHelper.cancelDaily(context)
                                scope.launch { snackbarHost.showSnackbar("Notifikasi harian dinonaktifkan") }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF8FA876),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.4f)
                        )
                    )
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.Lock,
                text = "Ubah Password",
                onClick = onNavigateToChangePassword,
                primaryColor = primaryColor
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Logout Button
    Button(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8FA876)
        )
    ) {
        Text(
            "Keluar",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
    primaryColor: Color,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = text,
                    tint = primaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text,
                color = Color(0xFF2D3E1F),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }

        if (trailingContent != null) {
            trailingContent()
        } else if (!isDestructive) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun scheduleDailyNotification(
    context: android.content.Context,
    hour: Int,
    minute: Int,
    scope: CoroutineScope,
    host: SnackbarHostState
) {
    com.example.lifin.notification.NotificationHelper.scheduleDaily(context, hour, minute)
    scope.launch { host.showSnackbar("Notifikasi diatur untuk ${String.format("%02d:%02d", hour, minute)}") }
}

@Composable
private fun LoadingOverlay() {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LogoutDialog(
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout Icon",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Text(
                "Anda yakin ingin keluar dari akun ini?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onError,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Ya, Keluar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }
        }
    )
}
