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
import com.example.lifin.R
import com.example.lifin.ui.components.OtherInformationSection
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.border
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onHealthNoteAdded: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onProfileClick = onNavigateToProfile,
                onCalendarClick = onNavigateToCalendar
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
                            text = "Hello, everyone!",
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

                        // Carousel: 3 cards
                        val pagerState = rememberPagerState(pageCount = { 3 })
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
                                        rightImages = listOf(R.drawable.botolpink, R.drawable.botolputih)
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
                            HealthNoteForm(
                                onSave = { note ->
                                    // Validasi sederhana lalu trigger callback ke NavGraph untuk menandai tanggal hijau
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
                                    onHealthNoteAdded()
                                    showHealthNoteForm = false
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Catatan tersimpan") }
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
            .height(120.dp),
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
            // Right images stacked/horizontal
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rightImages.forEach { res ->
                    Image(
                        painter = painterResource(id = res),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Crop
                    )
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
    val icon: Int? = null
)

// Health metrics data
val healthMetrics = listOf(
    HealthMetric("160", "cm", "", Color(0xFF89C2FF), R.drawable.jerapah),
    HealthMetric("120/80", "", "", Color(0xFF3F99F8), R.drawable.termometer),
    HealthMetric("45", "", "", Color(0xFFFF8989), R.drawable.anaksenam),
    HealthMetric("100", "mg/dL", "", Color(0xFF89FFD0), R.drawable.ukuran),
    HealthMetric("180", "kcal", "", Color(0xFFC889FF), R.drawable.brokoli),
    HealthMetric("70", "kg", "", Color(0xFFFF8989), R.drawable.timbangan)
)

@Composable
fun HealthMetricCard(metric: HealthMetric) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = metric.backgroundColor
        )
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
                    fontSize = 32.sp,
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
                        .size(50.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onProfileClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF738A45)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                }
            },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.Gray
                )
            },
            selected = false,
            onClick = onCalendarClick
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.Gray
                )
            },
            selected = false,
            onClick = onProfileClick
        )
    }
}

// Data model for health note
private data class HealthNote(
    val beratBadan: String,
    val tinggiBadan: String,
    val tekananDarah: String,
    val gulaDarah: String,
    val aktivitas: String,
    val nutrisi: String
)

@Composable
private fun HealthNoteForm(onSave: (HealthNote) -> Unit) {
    // Field states
    var berat by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var tekanan by remember { mutableStateOf("") }
    var gula by remember { mutableStateOf("") }
    var aktivitas by remember { mutableStateOf("") }
    var nutrisi by remember { mutableStateOf("") }
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
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledInput(label = "Berat Badan :", value = berat, placeholder = "kg", onChange = { berat = it })
                    LabeledInput(label = "Tekanan Darah :", value = tekanan, placeholder = "120/80", onChange = { tekanan = it })
                    LabeledInput(label = "Aktivitas :", value = aktivitas, placeholder = "menit", onChange = { aktivitas = it })
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledInput(label = "Tinggi Badan :", value = tinggi, placeholder = "cm", onChange = { tinggi = it })
                    LabeledInput(label = "Gula Darah :", value = gula, placeholder = "mg/dL", onChange = { gula = it })
                    LabeledInput(label = "Nutrisi :", value = nutrisi, placeholder = "makanan", onChange = { nutrisi = it })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (!isSaving) {
                        isSaving = true
                        fieldErrors = emptyList() // clear previous
                        val note = HealthNote(berat, tinggi, tekanan, gula, aktivitas, nutrisi)
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
private fun LabeledInput(label: String, value: String, placeholder: String, onChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4F5F2E))
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = Color(0xFF4F5F2E).copy(alpha = 0.5f)) },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF3FCE9),
                unfocusedContainerColor = Color(0xFFF3FCE9)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
