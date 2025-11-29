package com.example.lifin.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.zIndex
import com.example.lifin.R

// Data model untuk stacked cards (dipindahkan dari CalendarScreen)
data class HealthCardModel(
    val id: Int,
    val title: String,
    val color: Color,
    val type: HealthCardType
)

enum class HealthCardType {
    AKTIVITAS_FISIK,
    GULA_DARAH,
    TEKANAN_DARAH,
    KALORI
}

@Composable
fun OtherInformationSection() {
    Text(
        "Other Information",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(16.dp))
    StackedCardsSection()
}

@Composable
fun StackedCardsSection() {
    val cards = listOf(
        HealthCardModel(0, "Aktivitas Fisik", Color(0xFF2ECC71), HealthCardType.AKTIVITAS_FISIK),
        HealthCardModel(1, "Gula Darah", Color(0xFF5DADE2), HealthCardType.GULA_DARAH),
        HealthCardModel(2, "Tekanan Darah", Color(0xFFEC407A), HealthCardType.TEKANAN_DARAH),
        HealthCardModel(3, "Kalori", Color(0xFFF4D03F), HealthCardType.KALORI)
    )

    var expandedCardId by remember { mutableStateOf<Int?>(3) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        cards.forEachIndexed { index, card ->
            val isSelected = expandedCardId == card.id
            val targetOffset = if (isSelected) 0.dp else (index * 60).dp
            val animatedOffset by animateDpAsState(
                targetValue = targetOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ), label = "offsetAnim"
            )
            val zIndexValue = if (isSelected) 10f else index.toFloat()

            HealthCardItem(
                card = card,
                offsetY = animatedOffset,
                zIndex = zIndexValue,
                isExpanded = isSelected,
                onClick = { expandedCardId = card.id }
            )
        }
    }
}

@Composable
fun HealthCardItem(
    card: HealthCardModel,
    offsetY: androidx.compose.ui.unit.Dp,
    zIndex: Float,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isExpanded) 350.dp else 280.dp)
            .offset(y = offsetY)
            .zIndex(zIndex)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.White, RoundedCornerShape(50.dp))
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            when (card.type) {
                HealthCardType.AKTIVITAS_FISIK -> AktivitasFisikContent()
                HealthCardType.GULA_DARAH -> GulaDarahContent()
                HealthCardType.TEKANAN_DARAH -> TekananDarahContent()
                HealthCardType.KALORI -> KaloriContent()
            }
        }
    }
}

@Composable
fun AktivitasFisikContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("1. Lari", color = Color.Black, fontSize = 14.sp)
            Text("2. Tenis", color = Color.Black, fontSize = 14.sp)
            Text("3. Bersepeda", color = Color.Black, fontSize = 14.sp)
            Text("4. Berenang", color = Color.Black, fontSize = 14.sp)
            Text("5. Sepak Bola", color = Color.Black, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.sekelompokolahraga),
            contentDescription = "Aktivitas Fisik",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
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
        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(8.dp))
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

