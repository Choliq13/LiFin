package com.example.lifin.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextStyle
import com.example.lifin.R
import com.example.lifin.ui.components.OtherInformationSection
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.LocalTextStyle
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.border
import com.example.lifin.ui.components.BottomNavigationBar
import com.example.lifin.ui.components.BottomNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onHealthNoteAdded: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }

    // Ambil nama user dari preferences
    val firstName = remember { prefs.getProfileFirstName() }
    val lastName = remember { prefs.getProfileLastName() }
    val fullName = remember {
        when {
            firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
            firstName.isNotBlank() -> firstName
            lastName.isNotBlank() -> lastName
            else -> "everyone"
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = BottomNavItem.Home,
                onHome = onNavigateToHome,
                onCalendar = onNavigateToCalendar,
                onProfile = onNavigateToProfile,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White) // ensure content area is white
        ) {
            // Background Image (keep at top)
            Image(
                painter = painterResource(id = R.drawable.bglanding),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.TopCenter)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                        Text(
                            text = "Hello, $fullName!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Carousel: 3 cards dengan AUTO-SCROLL
                        val pagerState = rememberPagerState(pageCount = { 3 })

                        // Auto-scroll effect
                        LaunchedEffect(pagerState) {
                            while (true) {
                                kotlinx.coroutines.delay(3000) // 3 detik
                                val nextPage = (pagerState.currentPage + 1) % 3
                                pagerState.animateScrollToPage(nextPage)
                            }
                        }

                        Column {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                when (page) {
                                    0 -> ReminderCard(
                                        title = "Udah minum hari ini ?",
                                        subtitle = "Jangan lupa minum air 8 gelas perhari !",
                                        backgroundColor = Color(0xFFA9BF93),
                                        rightImages = listOf(R.drawable.botolpink)  // Hapus botolputih
                                    )
                                    1 -> ReminderCard(
                                        title = "Bagaimana perasaanmu sekarang ?",
                                        subtitle = "Jangan lupa istirahat yang cukup ya,\nkarena badan kamu juga butuh perhatian",
                                        backgroundColor = Color(0xFFBFE3EB),
                                        rightImages = listOf(R.drawable.jamdinding)
                                    )
                                    2 -> ReminderCard(
                                        title = "Udah makan apa hari ini ?",
                                        subtitle = "Jaga pola makan,\nkurangin minyak dan makanan pedas",
                                        backgroundColor = Color(0xFFE0C8A5),
                                        rightImages = listOf(R.drawable.sarapan)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Simple pager indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(3) { i ->
                                    val selected = i == pagerState.currentPage
                                    Box(
                                        modifier = Modifier
                                            .size(if (selected) 10.dp else 8.dp)
                                            .clip(CircleShape)
                                            .background(if (selected) Color(0xFF738A45) else Color.LightGray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                            }
                        }
                        // After carousel indicator insert Add Health Note toggle & form
                        var showHealthNoteForm by remember { mutableStateOf(false) }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Toggle Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .border(1.dp, Color(0xFF4F5F2E), RoundedCornerShape(50.dp))
                                .clickable { showHealthNoteForm = !showHealthNoteForm },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF4F5F2E))
                                Text(
                                    text = "Add your health note",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF4F5F2E)
                                )
                            }
                        }
                        if (showHealthNoteForm) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val context = LocalContext.current
                            val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }

                            HealthNoteForm(
                                onSave = { note ->
                                    // Validasi sederhana
                                    val hasData = listOf(
                                        note.beratBadan,
                                        note.tinggiBadan,
                                        note.tekananDarah,
                                        note.gulaDarah,
                                        note.aktivitas,
                                        note.nutrisi
                                    ).any { it.isNotBlank() }

                                    if (!hasData) {
                                        coroutineScope.launch { snackbarHostState.showSnackbar("Isi minimal satu field dulu") }
                                        return@HealthNoteForm
                                    }

                                    // Simpan data lengkap ke EncryptedPreferences
                                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                                    val healthData = com.example.lifin.data.local.HealthData(
                                        beratBadan = note.beratBadan,
                                        tinggiBadan = note.tinggiBadan,
                                        tekananDarah = note.tekananDarah,
                                        gulaDarah = note.gulaDarah,
                                        aktivitas = note.aktivitas,
                                        nutrisi = note.nutrisi,
                                        // Nutrisi details
                                        menuMakanan = note.menuMakanan,
                                        sesiMakan = note.sesiMakan,
                                        makananUtama = note.makananUtama,
                                        makananPendamping = note.makananPendamping,
                                        karbohidrat = note.karbohidrat,
                                        protein = note.protein,
                                        lemak = note.lemak,
                                        kalori = note.kalori,
                                        // Aktivitas details
                                        durasi = note.durasi,
                                        jenisAktivitas = note.jenisAktivitas
                                    )
                                    prefs.saveHealthData(today, healthData)

                                    // Trigger callback untuk menandai tanggal di kalender
                                    onHealthNoteAdded()

                                    showHealthNoteForm = false
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Catatan tersimpan! Lihat di Calendar") }
                                }
                            )
                        }
                    }
                    // Health Metrics Grid as single item
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().height(400.dp)
                        ) {
                            items(healthMetrics) { metric ->
                                HealthMetricCard(metric)
                            }
                        }
                    }
                    // Insert Other Information Section
                    item {
                        OtherInformationSection()
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    // Bottom spacer for nav bar overlap
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ReminderCard composable used in carousel
@Composable
private fun ReminderCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    rightImages: List<Int>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),  // Diperbesar dari 120dp ke 150dp
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            // Right images dengan bentuk yang rapi
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rightImages.forEach { res ->
                    // Ukuran lebih besar untuk single image (seperti jamdinding)
                    val imageSize = if (rightImages.size == 1) 100.dp else 70.dp

                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),  // Padding lebih besar untuk gambar lebih terlihat
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

// Data class for health metrics
data class HealthMetric(
    val value: String,
    val unit: String,
    val label: String = "",
    val backgroundColor: Color,
    val icon: Int? = null,
    val size: CardSize = CardSize.MEDIUM,  // Ukuran card
    val imageSize: ImageSize = ImageSize.MEDIUM  // Ukuran gambar
)

enum class CardSize {
    SMALL,   // 100dp height
    MEDIUM,  // 120dp height (default)
    LARGE    // 160dp height
}

enum class ImageSize {
    SMALL,   // 40dp
    MEDIUM,  // 50dp (default)
    LARGE,   // 70dp
    XLARGE   // 90dp
}

// Health metrics data dengan ukuran card dan gambar yang BERAGAM
val healthMetrics = listOf(
    HealthMetric(
        "160", "cm", "",
        Color(0xFF89C2FF),
        R.drawable.jerapah,
        size = CardSize.LARGE,      // Card besar
        imageSize = ImageSize.XLARGE // Gambar extra besar (jerapah tinggi)
    ),
    HealthMetric(
        "120/80", "", "",
        Color(0xFF3F99F8),
        R.drawable.termometer,
        size = CardSize.MEDIUM,     // Card medium
        imageSize = ImageSize.LARGE  // Gambar besar
    ),
    HealthMetric(
        "45", "", "",
        Color(0xFFFF8989),
        R.drawable.anaksenam,
        size = CardSize.SMALL,      // Card kecil
        imageSize = ImageSize.MEDIUM // Gambar medium
    ),
    HealthMetric(
        "100", "mg/dL", "",
        Color(0xFF89FFD0),
        R.drawable.ukuran,
        size = CardSize.MEDIUM,     // Card medium
        imageSize = ImageSize.SMALL  // Gambar kecil
    ),
    HealthMetric(
        "180", "kcal", "",
        Color(0xFFC889FF),
        R.drawable.brokoli,
        size = CardSize.LARGE,      // Card besar
        imageSize = ImageSize.LARGE  // Gambar besar (brokoli)
    ),
    HealthMetric(
        "70", "kg", "",
        Color(0xFFFF8989),
        R.drawable.timbangan,
        size = CardSize.SMALL,      // Card kecil
        imageSize = ImageSize.MEDIUM // Gambar medium
    )
)

@Composable
fun HealthMetricCard(metric: HealthMetric) {
    // Tentukan height berdasarkan size
    val cardHeight = when (metric.size) {
        CardSize.SMALL -> 100.dp
        CardSize.MEDIUM -> 120.dp
        CardSize.LARGE -> 160.dp
    }

    // Tentukan ukuran gambar
    val imageSize = when (metric.imageSize) {
        ImageSize.SMALL -> 40.dp
        ImageSize.MEDIUM -> 50.dp
        ImageSize.LARGE -> 70.dp
        ImageSize.XLARGE -> 90.dp
    }

    // Tentukan font size value berdasarkan ukuran card
    val valueFontSize = when (metric.size) {
        CardSize.SMALL -> 24.sp
        CardSize.MEDIUM -> 32.sp
        CardSize.LARGE -> 36.sp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = metric.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = metric.value,
                    fontSize = valueFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (metric.unit.isNotEmpty()) {
                    Text(
                        text = metric.unit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            if (metric.icon != null) {
                Image(
                    painter = painterResource(id = metric.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .align(Alignment.BottomEnd),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}


// Data model for health note
private data class HealthNote(
    val beratBadan: String,
    val tinggiBadan: String,
    val tekananDarah: String,
    val gulaDarah: String,
    val aktivitas: String,
    val nutrisi: String,
    // Nutrisi details
    val menuMakanan: String = "",
    val sesiMakan: String = "",
    val makananUtama: String = "",
    val makananPendamping: String = "",
    val karbohidrat: String = "",
    val protein: String = "",
    val lemak: String = "",
    val kalori: String = "",
    // Aktivitas details
    val durasi: String = "",
    val jenisAktivitas: String = ""
)

@Composable
private fun HealthNoteForm(onSave: (HealthNote) -> Unit) {
    // Tab state
    var selectedTab by remember { mutableStateOf(0) }

    // Data Dasar fields
    var berat by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var tekanan by remember { mutableStateOf("") }
    var gula by remember { mutableStateOf("") }

    // Nutrisi fields
    var menuMakanan by remember { mutableStateOf("") }
    var sesiMakan by remember { mutableStateOf("") }
    var makananUtama by remember { mutableStateOf("") }
    var makananPendamping by remember { mutableStateOf("") }
    var karbohidrat by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var lemak by remember { mutableStateOf("") }
    var kalori by remember { mutableStateOf("") }

    // Aktivitas fields
    var durasi by remember { mutableStateOf("") }
    var jenisAktivitas by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var fieldErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .border(1.dp, Color(0xFF4F5F2E), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Health note",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F5F2E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    text = "?",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                TabButton(
                    text = "Nutrisi",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                TabButton(
                    text = "Aktivitas",
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Data Dasar Tab
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(label = "Berat Badan :", value = berat, placeholder = "kg", onChange = { berat = it })
                            LabeledInput(label = "Tekanan Darah :", value = tekanan, placeholder = "120/80", onChange = { tekanan = it })
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(label = "Tinggi Badan :", value = tinggi, placeholder = "cm", onChange = { tinggi = it })
                            LabeledInput(label = "Gula Darah :", value = gula, placeholder = "mg/dL", onChange = { gula = it })
                        }
                    }
                }
                1 -> {
                    // Nutrisi Tab
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(
                                label = "Menu makanan :",
                                value = menuMakanan,
                                placeholder = "Nasi goreng",
                                onChange = { menuMakanan = it },
                                modifier = Modifier.weight(1f)
                            )
                            LabeledInput(
                                label = "Sesi makan :",
                                value = sesiMakan,
                                placeholder = "Sarapan",
                                onChange = { sesiMakan = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(
                                label = "Makanan utama :",
                                value = makananUtama,
                                placeholder = "Nasi",
                                onChange = { makananUtama = it },
                                modifier = Modifier.weight(1f)
                            )
                            LabeledInput(
                                label = "Makanan pendamping :",
                                value = makananPendamping,
                                placeholder = "Lauk",
                                onChange = { makananPendamping = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(
                                label = "Karbohidrat :",
                                value = karbohidrat,
                                placeholder = "gram",
                                onChange = { karbohidrat = it },
                                modifier = Modifier.weight(1f)
                            )
                            LabeledInput(
                                label = "Protein :",
                                value = protein,
                                placeholder = "gram",
                                onChange = { protein = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LabeledInput(
                                label = "Lemak :",
                                value = lemak,
                                placeholder = "gram",
                                onChange = { lemak = it },
                                modifier = Modifier.weight(1f)
                            )
                            LabeledInput(
                                label = "Kalori :",
                                value = kalori,
                                placeholder = "kkal",
                                onChange = { kalori = it },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                2 -> {
                    // Aktivitas Tab
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LabeledInput(
                            label = "Durasi :",
                            value = durasi,
                            placeholder = "menit",
                            onChange = { durasi = it }
                        )
                        LabeledInput(
                            label = "Jenis Aktivitas :",
                            value = jenisAktivitas,
                            placeholder = "Lari, Jalan, dll",
                            onChange = { jenisAktivitas = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (!isSaving) {
                        isSaving = true
                        fieldErrors = emptyList()
                        val note = HealthNote(
                            beratBadan = berat,
                            tinggiBadan = tinggi,
                            tekananDarah = tekanan,
                            gulaDarah = gula,
                            aktivitas = durasi,
                            nutrisi = menuMakanan,
                            menuMakanan = menuMakanan,
                            sesiMakan = sesiMakan,
                            makananUtama = makananUtama,
                            makananPendamping = makananPendamping,
                            karbohidrat = karbohidrat,
                            protein = protein,
                            lemak = lemak,
                            kalori = kalori,
                            durasi = durasi,
                            jenisAktivitas = jenisAktivitas
                        )
                        onSave(note)
                        isSaving = false
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(38.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF738A45))
            ) {
                Text("Simpan Catatan", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            // Error messages display
            if (fieldErrors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                fieldErrors.forEach { err ->
                    Text(err, color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF738A45) else Color(0xFFF3FCE9),
            contentColor = if (selected) Color.White else Color(0xFF4F5F2E)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 2.dp else 0.dp
        )
    ) {
        Text(
            text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun LabeledInput(
    label: String,
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4F5F2E))
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color(0xFF4F5F2E).copy(alpha = 0.5f), fontSize = 12.sp) },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF3FCE9),
                unfocusedContainerColor = Color(0xFFF3FCE9)
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
        )
    }
}
