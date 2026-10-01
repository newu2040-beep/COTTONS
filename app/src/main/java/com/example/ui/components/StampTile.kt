package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont

@Composable
fun StampTile(
    modifier: Modifier = Modifier,
    icon: String = "cherry",
    label: String = "",
    isStamped: Boolean = true,
    size: Dp = 68.dp,
    rotation: Float = 0f,
    onClick: (() -> Unit)? = null
) {
    val cottons = LocalCottons.current
    val shape = ScallopStampShape(radiusDp = 3.dp, pitchDp = 11.dp)

    Box(
        modifier = modifier
            .rotate(rotation)
            .size(size)
            .shadow(if (isStamped) 3.dp else 1.dp, shape)
            .clip(shape)
            .background(if (isStamped) Color(0xFFFFFDF8) else cottons.paper.copy(alpha = 0.5f))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Inner dashed line
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = cottons.inkSoft.copy(alpha = if (isStamped) 0.35f else 0.18f),
                topLeft = Offset(4f, 4f),
                size = androidx.compose.ui.geometry.Size(this.size.width - 8f, this.size.height - 8f),
                style = Stroke(
                    width = 1.2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f), 0f)
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emoji = when (icon.lowercase()) {
                "cherry" -> "🍒"
                "cat" -> "🐱"
                "star" -> "⭐"
                "coffee" -> "☕"
                "flower" -> "🌸"
                "book" -> "📖"
                "water" -> "💧"
                "tape" -> "📼"
                "sun" -> "☀️"
                "heart" -> "♥"
                else -> "💮"
            }

            Text(
                text = if (isStamped) emoji else "○",
                fontSize = if (isStamped) 24.sp else 20.sp,
                color = if (isStamped) cottons.primary else cottons.inkSoft.copy(alpha = 0.35f)
            )

            if (label.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = label,
                    fontFamily = MonoFont,
                    fontSize = 9.sp,
                    color = cottons.inkSoft
                )
            }
        }
    }
}
