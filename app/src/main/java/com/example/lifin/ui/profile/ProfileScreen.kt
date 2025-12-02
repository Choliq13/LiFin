package com.example.lifin.ui.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import android.content.Context
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

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = BottomNavItem.Profile,
                onHome = onNavigateToHome,
                onCalendar = onNavigateToCalendar,
                onProfile = onNavigateToProfile,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background image - Full screen (akan tertutup saat scroll)
            Image(
                painter = painterResource(id = R.drawable.bglanding),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            )

            // Profile header di tengah bagian atas (fixed position)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    val fullName = prefs.getProfileName().ifBlank { "Pengguna Baru" }
                    val avatarIndex = prefs.getProfileAvatarIndex()
                    val avatarColors = remember {
                        listOf(
                            Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFBBDEFB), Color(0xFFC8E6C9),
                            Color(0xFFFFF9C4), Color(0xFFD1C4E9), Color(0xFFFFE0B2)
                        )
                    }

                    // Avatar with white background
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
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
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Name with white background
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            // Scrollable Content dengan background F0F9E9
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // Spacer transparan untuk profile header area
                    Spacer(modifier = Modifier.height(350.dp))
                }
                
                item {
                    // Card besar dengan background F0F9E9 yang menutupi background
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        color = backgroundColor
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 32.dp, bottom = 24.dp)
                        ) {
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
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Single Card with all menu items
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
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
                icon = Icons.Default.Schedule,
                text = "Atur Waktu Notifikasi",
                onClick = { showTimePickerDialog = true },
                primaryColor = primaryColor,
                trailingContent = {
                    Text(
                        text = String.format("%02d:%02d", notifHour, notifMinute),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF293E00)
                    )
                }
            )
            
            // Tombol Test Notifikasi - hanya muncul jika notifikasi aktif
            if (notificationEnabled) {
                ProfileMenuItem(
                    icon = Icons.Default.NotificationsActive,
                    text = "Test Notifikasi",
                    onClick = {
                        // Trigger notifikasi langsung untuk testing
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                        com.example.lifin.notification.NotificationHelper.ensureChannel(context)
                        notificationManager.notify(
                            com.example.lifin.notification.NotificationHelper.NOTIFICATION_ID,
                            com.example.lifin.notification.NotificationHelper.buildNotification(context)
                        )
                        scope.launch { snackbarHost.showSnackbar("Notifikasi test dikirim!") }
                    },
                    primaryColor = primaryColor
                )
            }

            ProfileMenuItem(
                icon = Icons.Default.Lock,
                text = "Ubah Password",
                onClick = onNavigateToChangePassword,
                primaryColor = primaryColor
            )
        }
    }
    
    // Time Picker Dialog
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = notifHour,
            initialMinute = notifMinute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { hour, minute ->
                notifHour = hour
                notifMinute = minute
                prefs.setNotificationTime(hour, minute)
                showTimePickerDialog = false
                
                // Matikan notifikasi dan cancel alarm yang ada
                if (notificationEnabled) {
                    notificationEnabled = false
                    prefs.setNotificationEnabled(false)
                    com.example.lifin.notification.NotificationHelper.cancelDaily(context)
                }
                
                scope.launch { 
                    snackbarHost.showSnackbar("Waktu diatur ke ${String.format("%02d:%02d", hour, minute)}. Aktifkan notifikasi untuk menjadwalkan.") 
                }
            }
        )
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F9F3))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = text,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text,
                color = Color(0xFF293E00),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
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

@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Atur Waktu Notifikasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF293E00)
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour Picker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { selectedHour = (selectedHour + 1) % 24 }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase hour")
                    }
                    Text(
                        text = String.format("%02d", selectedHour),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF293E00)
                    )
                    IconButton(onClick = { selectedHour = if (selectedHour == 0) 23 else selectedHour - 1 }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease hour")
                    }
                }
                
                Text(
                    text = ":",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Minute Picker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { selectedMinute = (selectedMinute + 1) % 60 }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase minute")
                    }
                    Text(
                        text = String.format("%02d", selectedMinute),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF293E00)
                    )
                    IconButton(onClick = { selectedMinute = if (selectedMinute == 0) 59 else selectedMinute - 1 }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease minute")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF738A45))
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color(0xFF293E00))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
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
    val primaryColor = Color(0xFF293E00)
    val backgroundColor = Color(0xFFF5F9F3)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout Icon",
                    tint = Color(0xFF8FA876),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = primaryColor
                )
            }
        },
        text = {
            Text(
                "Anda yakin ingin keluar dari akun ini?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8FA876),
                    contentColor = Color.White
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Ya, Keluar", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor
                ),
                border = BorderStroke(1.dp, primaryColor)
            ) {
                Text("Batal", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
