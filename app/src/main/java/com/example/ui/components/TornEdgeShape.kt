package com.example.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class TornEdgeShape(
    private val seed: Int = 11,
    private val amplitude: Float = 5f,
    private val step: Float = 12f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val a = amplitude * density.density
        val s = step * density.density
        val rnd = Random(seed)

        val path = Path().apply {
            // Top jagged edge
            moveTo(0f, a)
            var x = 0f
            while (x < size.width) {
                x += s
                val nx = x.coerceAtMost(size.width)
                lineTo(nx, rnd.nextFloat() * a)
            }
            // Right edge (gentle wave)
            var y = 0f
            while (y < size.height) {
                y += s * 1.5f
                val ny = y.coerceAtMost(size.height)
                lineTo(size.width - rnd.nextFloat() * (a * 0.4f), ny)
            }
            // Bottom jagged edge
            x = size.width
            while (x > 0f) {
                x -= s
                val nx = x.coerceAtLeast(0f)
                lineTo(nx, size.height - rnd.nextFloat() * a)
            }
            // Left edge
            y = size.height
            while (y > 0f) {
                y -= s * 1.5f
                val ny = y.coerceAtLeast(0f)
                lineTo(rnd.nextFloat() * (a * 0.4f), ny)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

class ScallopStampShape(
    private val radiusDp: Dp = 4.dp,
    private val pitchDp: Dp = 14.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = radiusDp.value * density.density
        val pitch = pitchDp.value * density.density

        val path = Path().apply {
            moveTo(0f, 0f)
            // Top edge with notches
            var x = pitch / 2
            while (x < size.width - r) {
                lineTo(x - r, 0f)
                arcTo(
                    rect = Rect(x - r, -r, x + r, r),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                x += pitch
            }
            lineTo(size.width, 0f)

            // Right edge
            var y = pitch / 2
            while (y < size.height - r) {
                lineTo(size.width, y - r)
                arcTo(
                    rect = Rect(size.width - r, y - r, size.width + r, y + r),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                y += pitch
            }
            lineTo(size.width, size.height)

            // Bottom edge
            x = size.width - pitch / 2
            while (x > r) {
                lineTo(x + r, size.height)
                arcTo(
                    rect = Rect(x - r, size.height - r, x + r, size.height + r),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                x -= pitch
            }
            lineTo(0f, size.height)

            // Left edge
            y = size.height - pitch / 2
            while (y > r) {
                lineTo(0f, y + r)
                arcTo(
                    rect = Rect(-r, y - r, r, y + r),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                y -= pitch
            }
            close()
        }
        return Outline.Generic(path)
    }
}
