package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.SansFont

enum class CottonsTab(
    val title: String,
    val label: String,
    val emoji: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    TODAY("Today", "Today", "🏠", Icons.Filled.Home, Icons.Outlined.Home),
    NOTES("Notes", "Notes", "📝", Icons.Filled.Description, Icons.Outlined.Description),
    PLANNER("Planner", "Planner", "📅", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    STUDIO("Studio", "Studio", "🎨", Icons.Filled.Palette, Icons.Outlined.Palette),
    MORE("More", "Settings", "⚙️", Icons.Filled.Menu, Icons.Outlined.Menu)
}

@Composable
fun TinCaseNavBar(
    selectedTab: CottonsTab,
    onTabSelected: (CottonsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val cottons = LocalCottons.current
    val tinShape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(12.dp, tinShape, ambientColor = Color.Black.copy(alpha = 0.25f))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E7EA),
                        Color(0xFFCDD2D8),
                        Color(0xFFBDC4CB),
                        Color(0xFFD2D7DC)
                    )
                ),
                shape = tinShape
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color(0xFFA5ADB7),
                        Color.White.copy(alpha = 0.6f)
                    )
                ),
                shape = tinShape
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CottonsTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "tab_bounce"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(tab)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFFDF8), RoundedCornerShape(12.dp))
                                .border(1.dp, cottons.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) cottons.primary else cottons.ink.copy(alpha = 0.65f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontFamily = if (isSelected) HandFont else SansFont,
                            fontSize = if (isSelected) 12.sp else 11.sp,
                            color = if (isSelected) cottons.primary else cottons.ink.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
