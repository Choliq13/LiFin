package com.example.lifin.ui.profile

import android.app.TimePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.data.repository.AuthRepository
import com.example.lifin.data.repository.PinRepository
import com.example.lifin.ui.components.AppBottomNavBar
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
    var clearPin by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selected = BottomNavItem.Profile,
                onHome = onNavigateToHome,
                onCalendar = onNavigateToCalendar,
                onProfile = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileHeader(prefs)
                Spacer(modifier = Modifier.height(32.dp))
                ProfileMenuGroup(
                    prefs = prefs,
                    scope = scope,
                    snackbarHost = snackbarHost,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }

        if (showLogoutDialog) {
            LogoutDialog(
                clearPin = clearPin,
                onClearPinChange = { clearPin = it },
                isProcessing = loggingOut,
                onDismiss = { if (!loggingOut) showLogoutDialog = false },
                onConfirm = {
                    loggingOut = true
                    scope.launch {
                        val result = authRepository.logout()
                        if (clearPin) pinRepository.clearPin()
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

@Composable
private fun ProfileHeader(prefs: com.example.lifin.data.local.EncryptedPreferences) {
    val fullName = prefs.getProfileName().ifBlank { "Pengguna Baru" }
    val avatarIndex = prefs.getProfileAvatarIndex()
    val avatarColors = remember {
        listOf(
            Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFBBDEFB), Color(0xFFC8E6C9),
            Color(0xFFFFF9C4), Color(0xFFD1C4E9), Color(0xFFFFE0B2)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp)
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
        Text(fullName, fontWeight = FontWeight.Bold, fontSize = 22.sp)
    }
}

@Composable
private fun ProfileMenuGroup(
    prefs: com.example.lifin.data.local.EncryptedPreferences,
    scope: CoroutineScope,
    snackbarHost: SnackbarHostState,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var notificationEnabled by remember { mutableStateOf(prefs.isNotificationEnabled()) }
    var notifHour by remember { mutableStateOf(prefs.getNotificationHour()) }
    var notifMinute by remember { mutableStateOf(prefs.getNotificationMinute()) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Akun", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ProfileMenuItem(
                icon = Icons.Default.Edit,
                text = "Edit Profil",
                onClick = onNavigateToEditProfile
            )
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                text = "Ubah Password",
                onClick = onNavigateToChangePassword
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Pengaturan", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            NotificationSettingsItem(
                enabled = notificationEnabled,
                hour = notifHour,
                minute = notifMinute,
                onToggle = { checked ->
                    notificationEnabled = checked
                    prefs.setNotificationEnabled(checked)
                    if (checked) {
                        scheduleDailyNotification(context, notifHour, notifMinute, scope, snackbarHost)
                    } else {
                        com.example.lifin.notification.NotificationHelper.cancelDaily(context)
                        scope.launch { snackbarHost.showSnackbar("Notifikasi harian dinonaktifkan") }
                    }
                },
                onPickTime = { h, m ->
                    notifHour = h
                    notifMinute = m
                    prefs.setNotificationTime(h, m)
                    if (notificationEnabled) {
                        scheduleDailyNotification(context, h, m, scope, snackbarHost)
                    }
                }
            )
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                text = "Keluar",
                isDestructive = true,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            Icon(icon, contentDescription = text, tint = contentColor)
            Text(text, color = contentColor)
        }
        if (!isDestructive) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
private fun NotificationSettingsItem(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onToggle: (Boolean) -> Unit,
    onPickTime: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val time = String.format("%02d:%02d", hour, minute)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                TimePickerDialog(context, { _, h, m -> onPickTime(h, m) }, hour, minute, true).show()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Column {
                Text("Notifikasi Harian")
                Text(time, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
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
    clearPin: Boolean,
    onClearPinChange: (Boolean) -> Unit,
    isProcessing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Keluar") },
        text = {
            if (isProcessing) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Memproses...")
                }
            } else {
                Column {
                    Text("Anda yakin ingin keluar dari akun ini?")
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = clearPin, onCheckedChange = onClearPinChange)
                        Spacer(Modifier.width(8.dp))
                        Text("Hapus juga data PIN")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isProcessing,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Keluar")
            }
        },
        dismissButton = {
            if (!isProcessing) {
                TextButton(onClick = onDismiss) { Text("Batal") }
            }
        }
    )
}
