package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCottons

@Composable
fun WashiTape(
    modifier: Modifier = Modifier,
    color: Color? = null,
    width: Dp = 80.dp,
    height: Dp = 24.dp,
    rotation: Float = -2f,
    pattern: String = "DOTS"
) {
    val cottons = LocalCottons.current
    val tapeColor = color ?: cottons.tape

    Box(
        modifier = modifier
            .rotate(rotation)
            .width(width)
            .height(height)
            .shadow(2.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Tape body with serrated left/right ends
            val path = Path().apply {
                moveTo(6f, 0f)
                lineTo(w - 6f, 0f)

                // Right jagged cut
                var cy = 0f
                val teeth = 4
                val toothH = h / teeth
                for (i in 0 until teeth) {
                    lineTo(if (i % 2 == 0) w else w - 6f, cy + toothH / 2)
                    lineTo(w - 6f, cy + toothH)
                    cy += toothH
                }

                lineTo(6f, h)

                // Left jagged cut
                cy = h
                for (i in 0 until teeth) {
                    lineTo(if (i % 2 == 0) 0f else 6f, cy - toothH / 2)
                    lineTo(6f, cy - toothH)
                    cy -= toothH
                }
                close()
            }

            // Fill translucent tape
            drawPath(path, color = tapeColor.copy(alpha = 0.85f))

            // Subtle highlight line on top edge
            drawLine(
                color = Color.White.copy(alpha = 0.35f),
                start = Offset(8f, 2f),
                end = Offset(w - 8f, 2f),
                strokeWidth = 2f
            )

            // Pattern dots or diagonal stripes
            if (pattern == "DOTS") {
                var px = 14f
                while (px < w - 10f) {
                    drawCircle(Color.White.copy(alpha = 0.45f), radius = 2f, center = Offset(px, h * 0.35f))
                    drawCircle(Color.White.copy(alpha = 0.45f), radius = 2f, center = Offset(px + 7f, h * 0.65f))
                    px += 16f
                }
            } else {
                var sx = 10f
                while (sx < w) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(sx, 0f),
                        end = Offset(sx - 10f, h),
                        strokeWidth = 2f
                    )
                    sx += 14f
                }
            }
        }
    }
}
