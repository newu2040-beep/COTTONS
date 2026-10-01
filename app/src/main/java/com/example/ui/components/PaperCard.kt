package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.PaperStyle

@Composable
fun PaperCard(
    modifier: Modifier = Modifier,
    style: PaperStyle = PaperStyle.LINED_CREAM,
    rotation: Float = 0f,
    tape: Boolean = false,
    tapeColor: Color? = null,
    tapeRotation: Float = -2f,
    hasHoles: Boolean = false,
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val cottons = LocalCottons.current

    // Always solid paper background - never transparent
    val baseBg = when (style) {
        PaperStyle.LINED_BLUE -> if (cottons.isDark) Color(0xFF232B36) else Color(0xFFEFF4FB)
        PaperStyle.LINED_PINK -> if (cottons.isDark) Color(0xFF332328) else Color(0xFFFDF0F2)
        PaperStyle.KRAFT -> if (cottons.isDark) Color(0xFF2E2721) else Color(0xFFE8D7BE)
        PaperStyle.GRID -> if (cottons.isDark) Color(0xFF22262E) else Color(0xFFFBFBF6)
        PaperStyle.PLAIN -> cottons.paper
        PaperStyle.LINED_CREAM -> if (cottons.isDark) Color(0xFF262122) else Color(0xFFFFFDF5)
    }

    val ruledLineColor = when (style) {
        PaperStyle.LINED_BLUE -> Color(0xFFB5CCE8).copy(alpha = 0.4f)
        PaperStyle.LINED_PINK -> Color(0xFFF0BDC6).copy(alpha = 0.4f)
        PaperStyle.KRAFT -> Color(0xFFB89B77).copy(alpha = 0.35f)
        else -> cottons.inkSoft.copy(alpha = 0.16f)
    }

    val safeRotation = rotation.coerceIn(-3.5f, 3.5f)
    val safetyMargin = if (safeRotation != 0f) 6.dp else 0.dp
    val shape = TornEdgeShape(seed = (safeRotation * 10).toInt().coerceAtLeast(3) + 7)

    Box(
        modifier = modifier
            .padding(safetyMargin)
            .graphicsLayer { rotationZ = safeRotation }
    ) {
        Column(
            modifier = Modifier
                .shadow(elevation, shape, ambientColor = cottons.shadow, spotColor = cottons.shadow)
                .clip(shape)
                .background(baseBg)
                .drawBehind {
                    // Draw ruled lines if lined paper
                    if (style == PaperStyle.LINED_CREAM || style == PaperStyle.LINED_BLUE || style == PaperStyle.LINED_PINK) {
                        val gap = 28.dp.toPx()
                        var y = gap + 16.dp.toPx()
                        while (y < size.height - 8.dp.toPx()) {
                            drawLine(
                                color = ruledLineColor,
                                start = Offset(if (hasHoles) 36.dp.toPx() else 12.dp.toPx(), y),
                                end = Offset(size.width - 12.dp.toPx(), y),
                                strokeWidth = 1.dp.toPx()
                            )
                            y += gap
                        }
                        // Left margin vertical red rule
                        if (hasHoles) {
                            drawLine(
                                color = Color(0xFFE87E8E).copy(alpha = 0.4f),
                                start = Offset(32.dp.toPx(), 0f),
                                end = Offset(32.dp.toPx(), size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    } else if (style == PaperStyle.GRID) {
                        val cell = 18.dp.toPx()
                        var x = 0f
                        while (x < size.width) {
                            drawLine(cottons.inkSoft.copy(alpha = 0.12f), Offset(x, 0f), Offset(x, size.height), 1f)
                            x += cell
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(cottons.inkSoft.copy(alpha = 0.12f), Offset(0f, y), Offset(size.width, y), 1f)
                            y += cell
                        }
                    }

                    // Left binder punched holes
                    if (hasHoles) {
                        val holeRadius = 3.5.dp.toPx()
                        val hx = 14.dp.toPx()
                        val spacing = 32.dp.toPx()
                        var hy = 24.dp.toPx()
                        while (hy < size.height - 16.dp.toPx()) {
                            drawCircle(Color.Black.copy(alpha = 0.1f), holeRadius + 1f, Offset(hx, hy + 1f))
                            drawCircle(cottons.background, holeRadius, Offset(hx, hy))
                            hy += spacing
                        }
                    }
                }
                .padding(
                    start = if (hasHoles) 38.dp else 16.dp,
                    top = if (tape) 20.dp else 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            content = content
        )

        // Pinned washi tape at top center (drawn cleanly at top border)
        if (tape) {
            WashiTape(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-8).dp),
                color = tapeColor,
                rotation = tapeRotation
            )
        }
    }
}
