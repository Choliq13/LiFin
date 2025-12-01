package com.example.lifin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class BottomNavItem {
    Home,
    Calendar,
    Profile
}

private val GreenMain = Color(0xFF738A45)

@Composable
fun BottomNavigationBar(
    selected: BottomNavItem,
    onHome: () -> Unit,
    onCalendar: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Wrapper utama navbar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // ruang biar shadow nggak kepotong
        contentAlignment = Alignment.BottomCenter
    ) {
        // Navbar putih melengkung + shadow
        Surface(
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // cukup tinggi supaya bump nggak kepotong
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Slot Home
                NavSlot(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = selected == BottomNavItem.Home,
                    onClick = onHome
                )

                // Slot Calendar
                NavSlot(
                    icon = Icons.Default.CalendarToday,
                    label = "Calendar",
                    isSelected = selected == BottomNavItem.Calendar,
                    onClick = onCalendar
                )

                // Slot Profile
                NavSlot(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = selected == BottomNavItem.Profile,
                    onClick = onProfile
                )
            }
        }
    }
}

/**
 * Satu slot di navbar:
 * - kalau tidak selected → icon hijau biasa sejajar navbar
 * - kalau selected → lingkaran hijau nongol sedikit ke atas
 */
@Composable
private fun NavSlot(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        if (!isSelected) {
            // Icon biasa
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GreenMain,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onClick)
            )
        } else {
            // Bump lingkaran hijau
            Box(
                modifier = Modifier
                    .offset(y = (-10).dp) // naik dikit tapi tetap dalam tinggi Surface
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(GreenMain)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
