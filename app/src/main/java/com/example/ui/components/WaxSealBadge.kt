package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCottons
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WaxSealBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    color: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val cottons = LocalCottons.current
    val sealColor = color ?: cottons.seal

    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .size(size)
            .scale(scaleAnim.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = onClick != null
            ) {
                scope.launch {
                    scaleAnim.animateTo(0.85f, spring(stiffness = Spring.StiffnessHigh))
                    scaleAnim.animateTo(1.1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    scaleAnim.animateTo(1f)
                }
                onClick?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val r = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Organic melted wax perimeter path
            val waxPath = Path()
            val points = 24
            for (i in 0 until points) {
                val angle = (i.toFloat() / points) * (2f * Math.PI.toFloat())
                // subtle radial wobble
                val wobble = 1f + 0.08f * sin(i * 3f + 1.2f)
                val px = center.x + r * wobble * cos(angle)
                val py = center.y + r * wobble * sin(angle)
                if (i == 0) waxPath.moveTo(px, py) else waxPath.lineTo(px, py)
            }
            waxPath.close()

            // Soft drop shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.22f),
                radius = r * 0.95f,
                center = center + Offset(2f, 4f)
            )

            // Wax body with rich radial gradient (gloss highlight top-left)
            drawPath(
                path = waxPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        sealColor.copy(alpha = 0.95f),
                        sealColor,
                        sealColor.copy(red = sealColor.red * 0.7f, green = sealColor.green * 0.7f, blue = sealColor.blue * 0.7f)
                    ),
                    center = center - Offset(r * 0.3f, r * 0.3f),
                    radius = r * 1.2f
                )
            )

            // Outer embossed rim
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = r * 0.76f,
                center = center - Offset(1f, 1f),
                style = Stroke(width = 2.5f)
            )
            drawCircle(
                color = Color.Black.copy(alpha = 0.35f),
                radius = r * 0.76f,
                center = center + Offset(1f, 1.5f),
                style = Stroke(width = 2.5f)
            )

            // Inner debossed plate
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sealColor.copy(red = sealColor.red * 0.75f, green = sealColor.green * 0.75f, blue = sealColor.blue * 0.75f),
                        sealColor
                    ),
                    center = center,
                    radius = r * 0.72f
                ),
                radius = r * 0.72f,
                center = center
            )

            // Debossed botanical/cherry branch motif in center
            val motifPath = Path().apply {
                // Main stem curve
                moveTo(center.x - r * 0.28f, center.y + r * 0.32f)
                cubicTo(
                    center.x - r * 0.1f, center.y + r * 0.1f,
                    center.x, center.y - r * 0.15f,
                    center.x + r * 0.22f, center.y - r * 0.34f
                )
            }
            // Deboss shadow & highlight
            drawPath(motifPath, color = Color.Black.copy(alpha = 0.45f), style = Stroke(width = 4f))
            drawPath(motifPath, color = Color.White.copy(alpha = 0.3f), style = Stroke(width = 2f))

            // Pair of leaves
            drawOval(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(center.x - r * 0.15f, center.y - r * 0.12f),
                size = androidx.compose.ui.geometry.Size(r * 0.28f, r * 0.16f)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(center.x + r * 0.05f, center.y - r * 0.25f),
                size = androidx.compose.ui.geometry.Size(r * 0.24f, r * 0.14f)
            )

            // Gloss sheen crescent on upper edge
            drawArc(
                color = Color.White.copy(alpha = 0.22f),
                startAngle = 190f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(center.x - r * 0.85f, center.y - r * 0.85f),
                size = androidx.compose.ui.geometry.Size(r * 1.7f, r * 1.7f),
                style = Stroke(width = 3f)
            )
        }
    }
}
