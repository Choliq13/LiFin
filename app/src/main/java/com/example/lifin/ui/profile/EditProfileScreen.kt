package com.example.lifin.ui.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R
import java.util.Calendar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    onNavigateToChangePin: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }

    // State variables
    var firstName by remember { mutableStateOf(prefs.getProfileFirstName()) }
    var lastName by remember { mutableStateOf(prefs.getProfileLastName()) }
    var dob by remember { mutableStateOf(prefs.getProfileDob() ?: "") }
    var gender by remember { mutableStateOf(prefs.getProfileGender()) }
    var showGenderDropdown by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var avatarIndex by remember { mutableStateOf(prefs.getProfileAvatarIndex()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Colors
    val primaryColor = Color(0xFF293E00)
    val backgroundColor = Color(0xFFF0F9E9)
    val fieldContainerColor = Color.White

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = fieldContainerColor,
        unfocusedContainerColor = fieldContainerColor,
        disabledContainerColor = fieldContainerColor,
        focusedIndicatorColor = primaryColor,
        unfocusedIndicatorColor = primaryColor.copy(alpha = 0.7f),
        cursorColor = primaryColor,
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = primaryColor.copy(alpha = 0.8f),
        focusedTrailingIconColor = primaryColor,
        unfocusedTrailingIconColor = primaryColor.copy(alpha = 0.7f),
        disabledTrailingIconColor = primaryColor.copy(alpha = 0.7f)
    )

    val avatarColors = listOf(
        Color(0xFFFFCDD2), Color(0xFFF8BBD0), Color(0xFFBBDEFB), Color(0xFFC8E6C9),
        Color(0xFFFFF9C4), Color(0xFFD1C4E9), Color(0xFFFFE0B2)
    )
    val genderOptions = listOf("Laki-laki", "Perempuan", "Lainnya")

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            val mm = (m + 1).toString().padStart(2, '0')
            val dd = d.toString().padStart(2, '0')
            dob = "$y-$mm-$dd"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Image(
            painter = painterResource(id = R.drawable.bglanding),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.TopCenter)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profil", fontWeight = FontWeight.Bold, color = primaryColor) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Avatar Section
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(if (avatarIndex >= 0) avatarColors[avatarIndex.coerceIn(0, avatarColors.lastIndex)] else Color(0xFFE0E0E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Face,
                                        contentDescription = "Avatar",
                                        tint = Color(0xFF37474F),
                                        modifier = Modifier.size(72.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { showAvatarPicker = true }) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ubah Foto", color = primaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Form Fields Section
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Nama Depan & Belakang
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = firstName,
                                    onValueChange = { firstName = it.take(32) },
                                    label = { Text("Nama Depan") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = fieldColors
                                )
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it.take(32) },
                                    label = { Text("Nama Belakang") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = fieldColors
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))

                            // Tanggal lahir
                            OutlinedTextField(
                                value = if (dob.isNotBlank()) dob else "",
                                onValueChange = {},
                                label = { Text("Tanggal Lahir") },
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Pick Date") },
                                modifier = Modifier.fillMaxWidth().clickable(onClick = ::openDatePicker),
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Gender dropdown
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = gender,
                                    onValueChange = {},
                                    label = { Text("Jenis Kelamin") },
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            if (showGenderDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Toggle Gender"
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().clickable { showGenderDropdown = !showGenderDropdown },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = fieldColors
                                )
                                DropdownMenu(
                                    expanded = showGenderDropdown,
                                    onDismissRequest = { showGenderDropdown = false },
                                    modifier = Modifier.fillMaxWidth().background(fieldContainerColor)
                                ) {
                                    genderOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt, color = primaryColor) },
                                            onClick = { gender = opt; showGenderDropdown = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Action Buttons
                item {
                    // Tombol Ubah PIN
                    OutlinedButton(
                        onClick = { onNavigateToChangePin() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                        border = BorderStroke(1.dp, primaryColor)
                    ) { Text("Ubah PIN", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tombol Simpan
                    Button(
                        onClick = {
                            prefs.setProfileFirstName(firstName)
                            prefs.setProfileLastName(lastName)
                            prefs.setProfileName("$firstName $lastName".trim())
                            if (dob.isNotBlank()) prefs.setProfileDob(dob)
                            prefs.setProfileGender(gender)
                            scope.launch { snackbarHostState.showSnackbar("Profil berhasil diperbarui") }
                            onSaved()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Simpan Perubahan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // Extra space at the bottom
                }
            }

            // Avatar picker dialog
            if (showAvatarPicker) {
                AvatarPickerGrid(
                    colors = avatarColors,
                    onPick = { idx ->
                        avatarIndex = idx
                        prefs.setProfileAvatarIndex(idx)
                        showAvatarPicker = false
                    },
                    onDismiss = { showAvatarPicker = false }
                )
            }
        }
    }
}

@Composable
private fun AvatarPickerGrid(colors: List<Color>, onPick: (Int) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
        title = { Text("Pilih Avatar") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Pilih salah satu karakter:")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(colors.size) { idx ->
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(colors[idx])
                                .clickable { onPick(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Face, contentDescription = null, tint = Color.Black.copy(alpha = 0.6f), modifier = Modifier.size(40.dp))
                        }
                    }
                }
            }
        }
    )
}
