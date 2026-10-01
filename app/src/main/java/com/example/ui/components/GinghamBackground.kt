package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CottonsColors
import com.example.ui.theme.PatternKind
import kotlin.random.Random

fun Modifier.gingham(checkA: Color, checkB: Color, cell: Dp = 20.dp): Modifier = drawBehind {
    val c = cell.toPx()
    drawRect(checkB)
    var y = 0f
    var row = 0
    while (y < size.height) {
        var x = 0f
        var col = 0
        while (x < size.width) {
            if ((row + col) % 2 == 0) {
                drawRect(checkA.copy(alpha = 0.45f), Offset(x, y), Size(c, c))
            }
            x += c
            col++
        }
        y += c
        row++
    }
    // Deepen cross-intersections for authentic woven fabric look
    var yy = 0f
    var r = 0
    while (yy < size.height) {
        var xx = 0f
        var cc = 0
        while (xx < size.width) {
            if (r % 2 == 0 && cc % 2 == 0) {
                drawRect(checkA.copy(alpha = 0.35f), Offset(xx, yy), Size(c, c))
            }
            xx += c
            cc++
        }
        yy += c
        r++
    }
}

fun Modifier.polkaDots(dotColor: Color, bgColor: Color, spacing: Dp = 24.dp, radius: Dp = 3.5.dp): Modifier = drawBehind {
    drawRect(bgColor)
    val s = spacing.toPx()
    val r = radius.toPx()
    var y = s / 2
    var row = 0
    while (y < size.height) {
        var x = if (row % 2 == 0) s / 2 else s
        while (x < size.width) {
            drawCircle(dotColor.copy(alpha = 0.4f), radius = r, center = Offset(x, y))
            x += s
        }
        y += s * 0.866f
        row++
    }
}

fun Modifier.stripes(stripeColor: Color, bgColor: Color, width: Dp = 16.dp): Modifier = drawBehind {
    drawRect(bgColor)
    val w = width.toPx()
    var x = 0f
    var idx = 0
    while (x < size.width) {
        if (idx % 2 == 0) {
            drawRect(stripeColor.copy(alpha = 0.35f), Offset(x, 0f), Size(w, size.height))
        }
        x += w
        idx++
    }
}

fun Modifier.kraft(baseColor: Color): Modifier = drawBehind {
    drawRect(baseColor)
    val rnd = Random(12345)
    repeat(300) {
        val x = rnd.nextFloat() * size.width
        val y = rnd.nextFloat() * size.height
        val alpha = rnd.nextFloat() * 0.08f + 0.02f
        drawCircle(Color.Black.copy(alpha = alpha), radius = rnd.nextFloat() * 1.5f, center = Offset(x, y))
    }
}

fun Modifier.gridPattern(lineColor: Color, bgColor: Color, cellSize: Dp = 20.dp): Modifier = drawBehind {
    drawRect(bgColor)
    val c = cellSize.toPx()
    var x = 0f
    while (x < size.width) {
        drawLine(lineColor.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
        x += c
    }
    var y = 0f
    while (y < size.height) {
        drawLine(lineColor.copy(alpha = 0.25f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += c
    }
}

@Composable
fun CottonsBackground(
    colors: CottonsColors,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val patternModifier = when (colors.pattern) {
        PatternKind.GINGHAM -> Modifier.gingham(colors.checkA, colors.checkB)
        PatternKind.DOTS -> Modifier.polkaDots(colors.checkA, colors.checkB)
        PatternKind.STRIPES -> Modifier.stripes(colors.checkA, colors.checkB)
        PatternKind.KRAFT -> Modifier.kraft(colors.background)
        PatternKind.GRID -> Modifier.gridPattern(colors.inkSoft, colors.background)
    }

    Box(modifier = modifier.then(patternModifier), content = content)
}
