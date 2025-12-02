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
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

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
        ) {
            // Background image - Full screen
            Image(
                painter = painterResource(id = R.drawable.bglanding),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            )

            // Greeting di tengah bagian atas (fixed position)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hello, $fullName!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Scrollable Card Content dengan background F0F9E9
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // Spacer transparan untuk greeting area
                    Spacer(modifier = Modifier.height(400.dp))
                }
                
                item {
                    // Card besar yang membungkus semua konten dengan background F0F9E9
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        color = Color(0xFFF0F9E9)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 24.dp)
                        ) {
                            // Search Bar
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
                        }
                    }
                }
                
                item {
                    val pagerState = rememberPagerState(pageCount = { 3 })

                    // Auto-scroll effect
                    LaunchedEffect(pagerState) {
                        while (true) {
                            delay(3000)
                            val nextPage = (pagerState.currentPage + 1) % 3
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF0F9E9)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth(),
                                pageSpacing = 16.dp
                            ) { page ->
                                when (page) {
                                    0 -> ReminderCard(
                                        title = "Udah minum hari ini ?",
                                        subtitle = "Jangan lupa minum air 8 gelas perhari !",
                                        backgroundColor = Color(0xFFA9BF93),
                                        rightImages = listOf(R.drawable.botolpink)
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
                            Spacer(modifier = Modifier.height(12.dp))
                            // Pager indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(3) { i ->
                                    val selected = i == pagerState.currentPage
                                    Box(
                                        modifier = Modifier
                                            .size(
                                                width = if (selected) 24.dp else 8.dp,
                                                height = 8.dp
                                            )
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (selected) Color(0xFF738A45) else Color.LightGray.copy(alpha = 0.5f))
                                    )
                                    if (i < 2) Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
                
                // After carousel indicator insert Add Health Note toggle & form
                item {
                    var showHealthNoteForm by remember { mutableStateOf(false) }
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF0F9E9)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
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
                    }
                }
                
                // Health Metrics Grid as single item
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF0F9E9)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Health Metrics",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF293E00)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth().height(450.dp)
                            ) {
                                items(healthMetrics) { metric ->
                                    HealthMetricCard(metric)
                                }
                            }
                        }
                    }
                }
                
                // Insert Other Information Section
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF0F9E9)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Header Card untuk Other Information
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box {
                                        // Text dengan outline hitam
                                        Text(
                                            text = "Other Information",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            style = androidx.compose.ui.text.TextStyle(
                                                drawStyle = Stroke(
                                                    width = 3f,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        )
                                        // Text putih di atas
                                        Text(
                                            text = "Other Information",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            OtherInformationSection()
                            Spacer(modifier = Modifier.height(32.dp))
                            // Bottom spacer for nav bar overlap
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
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
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 24.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black.copy(alpha = 0.75f),
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Right images dengan bentuk yang rapi
            Box(
                contentAlignment = Alignment.Center
            ) {
                rightImages.forEach { res ->
                    val imageSize = if (rightImages.size == 1) 90.dp else 70.dp

                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize - 16.dp),
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
    val label: String,
    val backgroundColor: Color,
    val icon: Int? = null
)

// Health metrics data dengan desain profesional
val healthMetrics = listOf(
    HealthMetric(
        value = "160",
        unit = "cm",
        label = "Tinggi Badan",
        backgroundColor = Color(0xFF6BA3D8),
        icon = R.drawable.jerapah
    ),
    HealthMetric(
        value = "120/80",
        unit = "mmHg",
        label = "Tekanan Darah",
        backgroundColor = Color(0xFF5B9BD5),
        icon = R.drawable.termometer
    ),
    HealthMetric(
        value = "70",
        unit = "kg",
        label = "Berat Badan",
        backgroundColor = Color(0xFFFF8FA3),
        icon = R.drawable.timbangan
    ),
    HealthMetric(
        value = "100",
        unit = "mg/dL",
        label = "Gula Darah",
        backgroundColor = Color(0xFF7DD8C4),
        icon = R.drawable.ukuran
    ),
    HealthMetric(
        value = "180",
        unit = "kcal",
        label = "Nutrisi",
        backgroundColor = Color(0xFFB89FD9),
        icon = R.drawable.brokoli
    ),
    HealthMetric(
        value = "45",
        unit = "menit",
        label = "Aktivitas",
        backgroundColor = Color(0xFFFF9B9B),
        icon = R.drawable.anaksenam
    )
)

@Composable
fun HealthMetricCard(metric: HealthMetric) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = metric.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Label di atas
            Text(
                text = metric.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.95f),
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Value dan unit di tengah-kiri
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = metric.value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 32.sp
                )
                if (metric.unit.isNotEmpty()) {
                    Text(
                        text = metric.unit,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Icon di kanan bawah dengan opacity
            if (metric.icon != null) {
                Image(
                    painter = painterResource(id = metric.icon),
                    contentDescription = metric.label,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp),
                    contentScale = ContentScale.Fit,
                    alpha = 0.85f
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
                    text = "Umum",
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
                                placeholder = "Ayam rebus",
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
