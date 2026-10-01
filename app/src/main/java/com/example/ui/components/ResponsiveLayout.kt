package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CottonsColors
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.SerifFont

enum class WindowWidthClass {
    COMPACT,   // < 600dp: Standard phones, single column, bottom nav
    MEDIUM,    // 600dp..839dp: Foldables unfolded, small tablets, nav rail
    EXPANDED   // >= 840dp: Large tablets, desktop/DeX, nav rail + list-detail
}

val LocalWindowWidthClass = compositionLocalOf { WindowWidthClass.COMPACT }

@Composable
fun rememberWindowWidthClass(): WindowWidthClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    return when {
        screenWidth < 600.dp -> WindowWidthClass.COMPACT
        screenWidth < 840.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.EXPANDED
    }
}

/**
 * Wraps content in a centered container with a maximum width on tablets/expanded screens.
 * Prevents awkward stretching on wide screens while occupying full width on phones.
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 840.dp,
    content: @Composable () -> Unit
) {
    val widthClass = LocalWindowWidthClass.current
    val horizontalPadding = if (widthClass == WindowWidthClass.COMPACT) 16.dp else 24.dp

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
        ) {
            content()
        }
    }
}

/**
 * Adaptive Navigation Rail used on Medium and Expanded screens (Foldables & Tablets).
 */
@Composable
fun TinCaseNavRail(
    selectedTab: CottonsTab,
    onTabSelected: (CottonsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val cottons = LocalCottons.current

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(88.dp)
            .shadow(6.dp, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(cottons.paper)
            .border(1.dp, cottons.inkSoft.copy(alpha = 0.2f), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top App Mark
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(cottons.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("💮", fontSize = 22.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Cottons",
                fontFamily = SerifFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = cottons.ink
            )
        }

        // Navigation Items
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val tabs = listOf(
                CottonsTab.TODAY,
                CottonsTab.NOTES,
                CottonsTab.PLANNER,
                CottonsTab.STUDIO,
                CottonsTab.MORE
            )

            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .sizeIn(minWidth = 56.dp, minHeight = 56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) cottons.paperAlt else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = tab.emoji, fontSize = 22.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        fontFamily = HandFont,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) cottons.primary else cottons.inkSoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Bottom Decorative Stamp
        Text(
            text = "Desk",
            fontFamily = SerifFont,
            fontSize = 10.sp,
            color = cottons.inkSoft
        )
    }
}
