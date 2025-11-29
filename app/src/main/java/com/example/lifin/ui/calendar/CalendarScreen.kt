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
import com.example.lifin.ui.components.AppBottomNavBar
import com.example.lifin.ui.components.BottomNavItem

// Removed stacked cards related data classes and composables (HealthCardModel, HealthCardType, StackedCardsSection, HealthCardItem, AktivitasFisikContent, GulaDarahContent, GulaDarahCard, TekananDarahContent, TekananDarahRow, KaloriContent, FoodItem)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
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
            AppBottomNavBar(
                selected = BottomNavItem.Calendar,
                onHome = onNavigateToHome,
                onCalendar = { },
                onProfile = onNavigateToProfile
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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { displayMonth = displayMonth.minusMonths(1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color(0xFF738A45))
                    }
                    Text(
                        text = displayMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { displayMonth = displayMonth.plusMonths(1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color(0xFF738A45))
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Calendar grid
                for (week in 0..5) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (dow in 0..6) {
                            val cellIndex = week * 7 + dow
                            val dayNumber = cellIndex - startDayOfWeekIndex + 1
                            if (dayNumber in 1..daysInMonth) {
                                val date = firstOfMonth.withDayOfMonth(dayNumber)
                                val iso = date.format(formatter)
                                val bg = when {
                                    loginDateIso == iso -> Color(0xFFFFF59D) // kuning prioritas
                                    noteDates.contains(iso) -> Color(0xFFC8E6C9) // hijau
                                    else -> Color.White
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(bg),
                                    contentAlignment = Alignment.Center
                                ) { Text(dayNumber.toString(), fontSize = 12.sp) }
                            } else {
                                Spacer(Modifier.weight(1f).height(42.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
                Spacer(Modifier.height(24.dp))
            }

            // Health Curve Chart
            item {
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

            // Progress Circle Stats
            item {
                ProgressStatsSection()

                Spacer(modifier = Modifier.height(20.dp))
            }

            // History Pencatatan
            item {
                Text(
                    "History Pencatatan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(historyItems) { item ->
                HistoryItem(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HealthCurveChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Simple bar chart representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Sample data - 7 bars (Sunday to Saturday)
                BarChartItem(heightPercent = 0.4f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.5f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.7f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.6f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.8f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.65f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
                BarChartItem(heightPercent = 0.7f, color1 = Color(0xFF56AAFF), color2 = Color(0xFFFF5699))
            }

            // Tooltip (Rectangle 65)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 20.dp)
                    .background(Color(0xFF56AAFF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "Rectangle 65",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Progress
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 0.87f },
                modifier = Modifier.size(140.dp),
                strokeWidth = 12.dp,
                color = Color(0xFF51E0F9),
                trackColor = Color(0xFFE0E0E0)
            )
            CircularProgressIndicator(
                progress = { 0.65f },
                modifier = Modifier.size(110.dp),
                strokeWidth = 12.dp,
                color = Color(0xFFFF1414),
                trackColor = Color.Transparent
            )
            Text(
                "87%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Stats
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItem(
                icon = "🔥",
                value = "1,820",
                unit = "kcal",
                color = Color(0xFF51E0F9)
            )
            StatItem(
                icon = "🔥",
                value = "50",
                unit = "mnt",
                color = Color(0xFFFF1414)
            )
            StatItem(
                icon = "🥛",
                value = "7",
                unit = "gelas",
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
