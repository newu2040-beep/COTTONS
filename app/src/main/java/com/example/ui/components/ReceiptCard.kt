package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont

class ReceiptZigzagShape(
    private val toothWidth: Float = 14f,
    private val toothHeight: Float = 7f
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val tw = toothWidth * density.density
        val th = toothHeight * density.density

        val path = Path().apply {
            moveTo(0f, th)
            var x = 0f
            var up = true
            while (x < size.width) {
                x += tw
                lineTo(x.coerceAtMost(size.width), if (up) 0f else th)
                up = !up
            }

            lineTo(size.width, size.height - th)

            x = size.width
            up = true
            while (x > 0f) {
                x -= tw
                lineTo(x.coerceAtLeast(0f), if (up) size.height else size.height - th)
                up = !up
            }

            lineTo(0f, th)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun ReceiptCard(
    modifier: Modifier = Modifier,
    receiptNo: String = "NO. 0042",
    title: String = "COTTONS CAFÉ & PRODUCTIVITY",
    content: @Composable ColumnScope.() -> Unit
) {
    val cottons = LocalCottons.current
    val shape = ReceiptZigzagShape()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape, ambientColor = cottons.shadow)
            .clip(shape)
            .background(cottons.paper)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = MonoFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = cottons.ink,
                letterSpacing = 1.sp
            )
            Text(
                text = receiptNo,
                fontFamily = MonoFont,
                fontSize = 11.sp,
                color = cottons.inkSoft
            )

            ReceiptDivider(Modifier.padding(vertical = 12.dp))

            content()

            ReceiptDivider(Modifier.padding(vertical = 12.dp))

            Text(
                text = "THANK YOU FOR FOCUSING TODAY ♥",
                fontFamily = MonoFont,
                fontSize = 10.sp,
                color = cottons.inkSoft,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun ReceiptItemRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val cottons = LocalCottons.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = MonoFont,
            fontSize = 11.sp,
            color = cottons.inkSoft
        )
        Text(
            text = value,
            fontFamily = MonoFont,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = cottons.ink
        )
    }
}

@Composable
fun ReceiptDivider(modifier: Modifier = Modifier) {
    val cottons = LocalCottons.current
    Canvas(modifier = modifier.fillMaxWidth().height(2.dp)) {
        drawLine(
            color = cottons.inkSoft.copy(alpha = 0.35f),
            start = Offset(0f, 1f),
            end = Offset(size.width, 1f),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f)
        )
    }
}
