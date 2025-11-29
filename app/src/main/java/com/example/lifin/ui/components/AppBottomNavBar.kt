package com.example.lifin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer

enum class BottomNavItem { Home, Calendar, Profile }

@Composable
fun AppBottomNavBar(
    selected: BottomNavItem,
    onHome: () -> Unit,
    onCalendar: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 84.dp
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // Batang putih rounded
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.BottomCenter),
            color = Color.White,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                NavIcon(
                    icon = Icons.Filled.Home,
                    content = "Home",
                    active = selected == BottomNavItem.Home,
                    onClick = onHome
                )
                Spacer(Modifier.width(40.dp))
                NavIcon(
                    icon = Icons.Filled.DateRange,
                    content = "Calendar",
                    active = selected == BottomNavItem.Calendar,
                    onClick = onCalendar
                )
            }
        }
        // Lingkaran profil mengambang
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-12).dp, y = (-12).dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF738A45))
                .clickable { onProfile() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun NavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val tint = if (active) Color(0xFF738A45) else Color(0xFF5F5F5F)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, label = "navScale")
    val elevation by animateDpAsState(if (pressed) 6.dp else 0.dp, label = "navElevation")

    Surface(
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(4.dp),
        color = Color.Transparent,
        shadowElevation = elevation
    ) {
        Icon(icon, contentDescription = content, tint = tint, modifier = Modifier.size(28.dp))
    }
}
