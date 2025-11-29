package com.example.lifin.ui.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var firstName by remember { mutableStateOf(prefs.getProfileFirstName()) }
    var lastName by remember { mutableStateOf(prefs.getProfileLastName()) }
    var dob by remember { mutableStateOf(prefs.getProfileDob() ?: "") }
    var gender by remember { mutableStateOf(prefs.getProfileGender()) }
    var showGenderDropdown by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var avatarIndex by remember { mutableStateOf(prefs.getProfileAvatarIndex()) }
    val avatarColors = listOf(
        Color(0xFFFFCDD2), // pink red (F)
        Color(0xFFF8BBD0), // pink (F)
        Color(0xFFBBDEFB), // blue (M)
        Color(0xFFC8E6C9), // green (M)
        Color(0xFFFFF9C4), // yellow (F)
        Color(0xFFD1C4E9), // purple (F)
        Color(0xFFFFE0B2), // orange (M)
    )
    val genderOptions = listOf("Laki-laki", "Perempuan", "Lainnya")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFE4F1D3),
        unfocusedContainerColor = Color(0xFFE4F1D3),
        disabledContainerColor = Color(0xFFE4F1D3),
        focusedIndicatorColor = Color(0xFF738A45),
        unfocusedIndicatorColor = Color(0xFF90A763),
        disabledIndicatorColor = Color(0xFF90A763),
        cursorColor = Color(0xFF738A45),
        focusedLabelColor = Color(0xFF4E5F2E),
        unfocusedLabelColor = Color(0xFF4E5F2E)
    )

    fun openDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(context, { _, y, m, d ->
            val mm = (m + 1).toString().padStart(2, '0')
            val dd = d.toString().padStart(2, '0')
            dob = "$y-$mm-$dd"
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        prefs.setProfileFirstName(firstName)
                        prefs.setProfileLastName(lastName)
                        prefs.setProfileName("$firstName $lastName".trim())
                        if (dob.isNotBlank()) prefs.setProfileDob(dob)
                        prefs.setProfileGender(gender)
                        scope.launch {
                            snackbarHostState.showSnackbar("Profil disimpan")
                        }
                        onSaved()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF738A45))
                ) {
                    Text("Simpan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Avatar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        if (avatarIndex >= 0) {
                            Icon(Icons.Filled.Face, contentDescription = "Avatar", tint = Color(0xFF37474F), modifier = Modifier.size(72.dp))
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                        }
                    }
                }
                TextButton(onClick = { showAvatarPicker = true }) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color(0xFF738A45),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ubah Foto", color = Color(0xFF738A45), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nama Depan & Belakang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it.take(32) },
                    label = { Text("Nama Depan") },
                    trailingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Edit Nama Depan", tint = Color(0xFF738A45)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it.take(32) },
                    label = { Text("Nama Belakang") },
                    trailingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Edit Nama Belakang", tint = Color(0xFF738A45)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tanggal lahir
            OutlinedTextField(
                value = if (dob.isNotBlank()) dob else "Tanggal Lahir",
                onValueChange = {},
                label = { Text("Tanggal Lahir") },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = { openDatePicker() }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Pick Date", tint = Color(0xFF738A45))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { openDatePicker() }),
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gender dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Jenis Kelamin") },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            if (showGenderDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Gender",
                            tint = Color(0xFF738A45)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGenderDropdown = !showGenderDropdown },
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors
                )
                DropdownMenu(expanded = showGenderDropdown, onDismissRequest = { showGenderDropdown = false }, modifier = Modifier.fillMaxWidth()) {
                    genderOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = { gender = opt; showGenderDropdown = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tombol Ubah PIN
            OutlinedButton(
                onClick = { onNavigateToChangePin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF738A45)),
                border = BorderStroke(1.dp, Color(0xFF738A45))
            ) { Text("Ubah PIN", fontWeight = FontWeight.Medium, fontSize = 16.sp) }


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
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
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
                            Icon(Icons.Filled.Face, contentDescription = "Avatar $idx", tint = Color(0xFF37474F), modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    )
}
