package com.example.lifin.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import java.text.SimpleDateFormat
import com.example.lifin.ui.components.BottomNavigationBar
import com.example.lifin.ui.components.BottomNavItem

// Removed stacked cards related data classes and composables (HealthCardModel, HealthCardType, StackedCardsSection, HealthCardItem, AktivitasFisikContent, GulaDarahContent, GulaDarahCard, TekananDarahContent, TekananDarahRow, KaloriContent, FoodItem)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
    // read dates
    val loginDateIso = remember { prefs.getLastLoginDateIso() }
    val noteDates = remember { prefs.getHealthNoteDates() }

    var displayMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val firstOfMonth = displayMonth
    val startDayOfWeekIndex = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = firstOfMonth.lengthOfMonth()
    val formatter = DateTimeFormatter.ISO_DATE

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = BottomNavItem.Calendar,
                onHome = onNavigateToHome,
                onCalendar = onNavigateToCalendar,
                onProfile = onNavigateToProfile,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FFEF))
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            // Health Curve Chart - PALING ATAS
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Health Curve",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                HealthCurveChart()

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Kalender - SETELAH HEALTH CURVE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header dengan bulan dan navigasi
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF8BC34A),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Prev",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = displayMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = { displayMonth = displayMonth.plusMonths(1) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                // Header hari (Sen, Sel, Rab, dll)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF293E00)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Calendar grid dengan desain lebih jelas
                for (week in 0..5) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (dow in 0..6) {
                            val cellIndex = week * 7 + dow
                            val dayNumber = cellIndex - startDayOfWeekIndex + 1
                            if (dayNumber in 1..daysInMonth) {
                                val date = firstOfMonth.withDayOfMonth(dayNumber)
                                val iso = date.format(formatter)
                                val bg = when {
                                    loginDateIso == iso -> Color(0xFFFFEB3B) // kuning terang untuk login
                                    noteDates.contains(iso) -> Color(0xFF8BC34A) // hijau terang untuk note
                                    else -> Color.White
                                }
                                val textColor = when {
                                    loginDateIso == iso -> Color(0xFF000000) // hitam
                                    noteDates.contains(iso) -> Color.White // putih
                                    else -> Color(0xFF293E00) // hijau tua
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(bg)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF293E00).copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                }
                            } else {
                                Spacer(Modifier.weight(1f).height(48.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(16.dp))

                // Legend / Keterangan warna
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Keterangan Login
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFEB3B))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF293E00).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(
                            "Login",
                            fontSize = 12.sp,
                            color = Color(0xFF293E00)
                        )
                    }

                    // Keterangan Health Note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF8BC34A))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF293E00).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Text(
                            "Health Note",
                            fontSize = 12.sp,
                            color = Color(0xFF293E00)
                        )
                    }
                }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }


            // Progress Circle Stats
            item {
                ProgressStatsSection()

                Spacer(modifier = Modifier.height(20.dp))
            }

            // History Pencatatan - DATA REAL
            item {
                Text(
                    "History Pencatatan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Ambil data real dari EncryptedPreferences
            val historyData = prefs.getAllHealthHistory()

            if (historyData.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Belum ada history.\nInput data kesehatan di Home!",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(historyData) { historyItem ->
                    RealHistoryItem(historyItem)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HealthCurveChart() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
    val healthDataList = remember { prefs.getLastSevenDaysHealthData() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Chart with Y-axis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Y-axis labels - Tinggi Badan (100-170) - KIRI
                Column(
                    modifier = Modifier
                        .width(32.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("170", fontSize = 10.sp, color = Color(0xFF56AAFF), fontWeight = FontWeight.Medium)
                    Text("160", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("150", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("140", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("130", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("120", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("110", fontSize = 10.sp, color = Color(0xFF56AAFF))
                    Text("100", fontSize = 10.sp, color = Color(0xFF56AAFF), fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Chart bars - DATA REAL dari input user
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (healthDataList.isEmpty()) {
                        // Tampilkan placeholder jika belum ada data
                        Text(
                            "Belum ada data.\nInput di Home!",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    } else {
                        // Tampilkan data real (max 7 hari terakhir)
                        healthDataList.take(7).forEach { (date, data) ->
                            val beratBadanValue = data.beratBadan.toFloatOrNull() ?: 0f
                            val tinggiBadanValue = data.tinggiBadan.toFloatOrNull() ?: 0f

                            // Normalize ke 0-1 range
                            val beratPercent = ((beratBadanValue - 10f) / (150f - 10f)).coerceIn(0f, 1f)
                            val tinggiPercent = ((tinggiBadanValue - 100f) / (200f - 100f)).coerceIn(0f, 1f)

                            BarChartItem(
                                heightPercent = maxOf(beratPercent, tinggiPercent),
                                color1 = Color(0xFF56AAFF),
                                color2 = Color(0xFFFF5699)
                            )
                        }

                        // Fill remaining bars jika kurang dari 7
                        repeat(7 - healthDataList.size.coerceAtMost(7)) {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Y-axis labels - Berat Badan (10-80) - KANAN
                Column(
                    modifier = Modifier
                        .width(32.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("80", fontSize = 10.sp, color = Color(0xFFFF5699), fontWeight = FontWeight.Medium)
                    Text("70", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("60", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("50", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("40", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("30", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("20", fontSize = 10.sp, color = Color(0xFFFF5699))
                    Text("10", fontSize = 10.sp, color = Color(0xFFFF5699), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend - Keterangan warna
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tinggi Badan
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF56AAFF), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Tinggi Badan",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Berat Badan
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFFF5699), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Berat Badan",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BarChartItem(heightPercent: Float, color1: Color, color2: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.height(150.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Blue bar
        Box(
            modifier = Modifier
                .width(12.dp)
                .fillMaxHeight(heightPercent * 0.5f)
                .background(color1, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Pink bar
        Box(
            modifier = Modifier
                .width(12.dp)
                .fillMaxHeight(heightPercent * 0.5f)
                .background(color2, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
        )
    }
}

@Composable
fun CalendarWidget(
    calendar: Calendar,
    onMonthChanged: (Calendar) -> Unit,
    selectedDates: List<Int>
) {
    // Create a copy to avoid mutating original when computing values
    val displayCal = calendar.clone() as Calendar
    displayCal.set(Calendar.DAY_OF_MONTH, 1)
    // Use non-deprecated locale creation
    val localeId = Locale.forLanguageTag("id-ID")
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", localeId)
    val monthYearLabel = monthYearFormat.format(displayCal.time).replaceFirstChar { it.titlecase(localeId) }
    val firstDayOfWeekIndex = displayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0..6 (Sunday=1)
    val daysInMonth = displayCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Month/Year Header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newCal = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                    onMonthChanged(newCal)
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous Month", tint = Color.Gray)
                }

                Text(
                    text = monthYearLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    val newCal = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                    onMonthChanged(newCal)
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Month", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day headers (S M T W T F S)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days with colored backgrounds
            Column {
                var dayCounter = 1
                repeat(6) { week ->
                    if (dayCounter <= daysInMonth) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(7) { dayOfWeek ->
                                if (week == 0 && dayOfWeek < firstDayOfWeekIndex) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else if (dayCounter <= daysInMonth) {
                                    CalendarDayItem(
                                        day = dayCounter,
                                        isSelected = selectedDates.contains(dayCounter),
                                        color = getDayColor(dayCounter, selectedDates),
                                        modifier = Modifier.weight(1f)
                                    )
                                    dayCounter++
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

fun getDayColor(day: Int, selectedDates: List<Int>): Color {
    if (!selectedDates.contains(day)) return Color.Transparent

    // Rainbow colors for selected days (matching design)
    val colors = listOf(
        Color(0xFFFF1414), // Red - 03
        Color(0xFFFF892F), // Orange - 04
        Color(0xFFFFE629), // Yellow - 05
        Color(0xFF77FF4A), // Green - 06
        Color(0xFF58BAFF), // Blue - 07
        Color(0xFF9B89FF), // Purple - 08
        Color(0xFFEF74FF)  // Pink - 09
    )

    val index = selectedDates.indexOf(day)
    return if (index in colors.indices) colors[index] else Color.Gray
}

@Composable
fun CalendarDayItem(
    day: Int,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString().padStart(2, '0'),
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ProgressStatsSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.lifin.data.local.EncryptedPreferences(context) }
    val healthHistory = remember { prefs.getAllHealthHistory() }

    // Hitung progress dari data yang ada
    val totalDaysWithData = healthHistory.size
    val progress = (totalDaysWithData / 30f).coerceAtMost(1f) // Target 30 hari
    val progressPercent = (progress * 100).toInt()

    // Hitung total aktivitas dan nutrisi
    val totalAktivitas = healthHistory.sumOf {
        it.data.aktivitas.toIntOrNull() ?: 0
    }
    val totalNutrisi = healthHistory.size // Jumlah hari input nutrisi

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Progress - OTOMATIS DARI DATA
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(140.dp),
                strokeWidth = 12.dp,
                color = Color(0xFF51E0F9),
                trackColor = Color(0xFFE0E0E0)
            )
            CircularProgressIndicator(
                progress = { progress * 0.75f },
                modifier = Modifier.size(110.dp),
                strokeWidth = 12.dp,
                color = Color(0xFFFF1414),
                trackColor = Color.Transparent
            )
            Text(
                "$progressPercent%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Stats - DATA REAL
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItem(
                icon = "📊",
                value = "$totalDaysWithData",
                unit = "hari",
                color = Color(0xFF51E0F9)
            )
            StatItem(
                icon = "🔥",
                value = "$totalAktivitas",
                unit = "mnt",
                color = Color(0xFFFF1414)
            )
            StatItem(
                icon = "🍎",
                value = "$totalNutrisi",
                unit = "meals",
                color = Color(0xFF9B89FF)
            )
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, unit: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color // gunakan parameter color di sini
        )
        Text(
            unit,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

data class HistoryItemData(
    val title: String,
    val date: String
)

val historyItems = listOf(
    HistoryItemData("Rabu, 18 November 2025", "Detail"),
    HistoryItemData("Jumat, 30 November 2025", "Detail")
)

@Composable
fun HistoryItem(item: HistoryItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                item.title,
                fontSize = 14.sp,
                color = Color.Black
            )

            TextButton(onClick = { /* Show detail */ }) {
                Text(
                    "Detail",
                    color = Color(0xFF56AAFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RealHistoryItem(historyItem: com.example.lifin.data.local.HealthHistoryItem) {
    var expanded by remember { mutableStateOf(false) }

    // Format tanggal untuk ditampilkan
    val dateFormatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val date = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(historyItem.date)
    } catch (e: Exception) {
        Date()
    }
    val formattedDate = dateFormatter.format(date ?: Date())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formattedDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) "Tutup" else "Detail",
                        color = Color(0xFF56AAFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Detail data (collapsed/expanded)
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F9F3), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (historyItem.data.beratBadan.isNotBlank()) {
                        HistoryDetailRow("Berat Badan", "${historyItem.data.beratBadan} kg")
                    }
                    if (historyItem.data.tinggiBadan.isNotBlank()) {
                        HistoryDetailRow("Tinggi Badan", "${historyItem.data.tinggiBadan} cm")
                    }
                    if (historyItem.data.tekananDarah.isNotBlank()) {
                        HistoryDetailRow("Tekanan Darah", historyItem.data.tekananDarah)
                    }
                    if (historyItem.data.gulaDarah.isNotBlank()) {
                        HistoryDetailRow("Gula Darah", "${historyItem.data.gulaDarah} mg/dL")
                    }
                    if (historyItem.data.aktivitas.isNotBlank()) {
                        HistoryDetailRow("Aktivitas", "${historyItem.data.aktivitas} menit")
                    }
                    if (historyItem.data.nutrisi.isNotBlank()) {
                        HistoryDetailRow("Nutrisi", historyItem.data.nutrisi)
                    }

                    // Nutrisi details
                    if (historyItem.data.menuMakanan.isNotBlank()) {
                        HistoryDetailRow("Menu Makanan", historyItem.data.menuMakanan)
                    }
                    if (historyItem.data.sesiMakan.isNotBlank()) {
                        HistoryDetailRow("Sesi Makan", historyItem.data.sesiMakan)
                    }
                    if (historyItem.data.karbohidrat.isNotBlank()) {
                        HistoryDetailRow("Karbohidrat", "${historyItem.data.karbohidrat}g")
                    }
                    if (historyItem.data.protein.isNotBlank()) {
                        HistoryDetailRow("Protein", "${historyItem.data.protein}g")
                    }
                    if (historyItem.data.kalori.isNotBlank()) {
                        HistoryDetailRow("Kalori", "${historyItem.data.kalori} kkal")
                    }

                    // Aktivitas details
                    if (historyItem.data.durasi.isNotBlank()) {
                        HistoryDetailRow("Durasi Aktivitas", "${historyItem.data.durasi} menit")
                    }
                    if (historyItem.data.jenisAktivitas.isNotBlank()) {
                        HistoryDetailRow("Jenis Aktivitas", historyItem.data.jenisAktivitas)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

