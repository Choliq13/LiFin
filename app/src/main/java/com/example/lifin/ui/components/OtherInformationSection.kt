package com.example.lifin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifin.R

// Data model for the new expandable cards
private data class InfoCardModel(
    val title: String,
    val icon: Int,
    val backgroundColor: Color,
    val content: @Composable () -> Unit
)

@Composable
fun OtherInformationSection() {
    val infoCards = listOf(
        InfoCardModel(
            title = "Aktivitas Fisik",
            icon = R.drawable.anaksenam,
            backgroundColor = Color(0xFFFF9B9B), // Pink - sama dengan Aktivitas di Health Metrics
            content = { AktivitasFisikContent() }
        ),
        InfoCardModel(
            title = "Gula Darah",
            icon = R.drawable.ukuran,
            backgroundColor = Color(0xFF7DD8C4), // Turquoise - tetap sama
            content = { GulaDarahContent() }
        ),
        InfoCardModel(
            title = "Tekanan Darah",
            icon = R.drawable.termometer,
            backgroundColor = Color(0xFF5B9BD5), // Biru tua - sama dengan Tekanan Darah di Health Metrics
            content = { TekananDarahContent() }
        ),
        InfoCardModel(
            title = "Asupan Kalori",
            icon = R.drawable.brokoli,
            backgroundColor = Color(0xFFB89FD9), // Ungu - sama dengan Nutrisi di Health Metrics
            content = { KaloriContent() }
        )
    )

    // State to keep track of which card is expanded. Null means all are collapsed.
    var expandedCard by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        infoCards.forEach { card ->
            ExpandableInfoCard(
                title = card.title,
                icon = card.icon,
                backgroundColor = card.backgroundColor,
                isExpanded = expandedCard == card.title,
                onToggle = {
                    expandedCard = if (expandedCard == card.title) null else card.title
                },
                content = card.content
            )
        }
    }
}

@Composable
private fun ExpandableInfoCard(
    title: String,
    icon: Int,
    backgroundColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = title,
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Black.copy(alpha = 0.6f)
                )
            }

            // Content Section
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}


// Keep the original content composables but maybe adjust padding/background if needed

@Composable
fun AktivitasFisikContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Aktivitas fisik adalah pergerakan otot rangka yang bikin tubuh membakar kalori. Bisa berupa olahraga maupun kegiatan sehari-hari.",
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                "Min 30 Menit / Hari",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("1. Lari", color = Color.Black, fontSize = 13.sp)
            Text("2. Tenis", color = Color.Black, fontSize = 13.sp)
            Text("3. Bersepeda", color = Color.Black, fontSize = 13.sp)
            Text("4. Berenang", color = Color.Black, fontSize = 13.sp)
            Text("5. Sepak Bola", color = Color.Black, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.sekelompokolahraga),
            contentDescription = "Aktivitas Fisik",
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun GulaDarahContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GulaDarahCard("Katosis", "<80 GDP - Puasa\n<95 Setelah Makan", Color(0xFF89C2FF))
            GulaDarahCard("Normal", "70-104 GDP - Puasa\n120-140 Setelah Makan", Color(0xFF89C2FF))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            GulaDarahCard("Pre Diabetes", ">105 GDP - Puasa\n>140 Setelah Makan", Color(0xFF89C2FF))
        }
    }
}

@Composable
fun GulaDarahCard(title: String, values: String, color: Color) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(values, fontSize = 11.sp, color = color, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TekananDarahContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TekananDarahRow("Tekanan Darah Normal", "<120 / <80")
        TekananDarahRow("Tekanan Darah Meningkat", "120-129 / <80")
        TekananDarahRow("Hipertensi Tahap 1", "130-139 / 80-89")
        TekananDarahRow("Hipertensi Tahap 2", "140+ / 90+")
        TekananDarahRow("Darurat Hipertensi", "180+ / 120+")
    }
}

@Composable
fun TekananDarahRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(label, fontSize = 12.sp, color = Color.Black, modifier = Modifier.padding(horizontal = 12.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(value, fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun KaloriContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Pria: 2.000 - 2.800", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Wanita: 1.600 - 2.200", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .background(Color(0xFFFF892F), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text("Makanan Tinggi Lemak", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FoodItem(R.drawable.dagingmerah, "Daging")
                FoodItem(R.drawable.rotigandum, "Roti")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FoodItem(R.drawable.telor, "Telur")
                FoodItem(R.drawable.nasi, "Nasi")
                FoodItem(R.drawable.kentang, "Kentang")
            }
        }
    }
}

@Composable
fun FoodItem(imageRes: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(label, fontSize = 11.sp, color = Color.Black)
        }
    }
}
