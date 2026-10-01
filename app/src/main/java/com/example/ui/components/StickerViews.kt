package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

data class StickerDefinition(
    val id: String,
    val name: String,
    val emoji: String,
    val drawableRes: Int? = null
)

object StickerPacks {
    val cozyItems = listOf(
        StickerDefinition("cat", "Sleeping Cat", "🐱", R.drawable.sticker_cat),
        StickerDefinition("cherry", "Cherries", "🍒", R.drawable.sticker_cherries),
        StickerDefinition("bow", "Ribbon Bow", "🎀"),
        StickerDefinition("tulip", "Tulip Bouquet", "🌷"),
        StickerDefinition("bear", "Teddy Bear", "🧸"),
        StickerDefinition("coffee", "Dream Café", "☕"),
        StickerDefinition("star", "Star Patch", "⭐"),
        StickerDefinition("heart", "Wax Heart", "💖"),
        StickerDefinition("leaves", "Olive Leaves", "🌿"),
        StickerDefinition("camera", "Vintage Film", "📷")
    )
}

@Composable
fun CozyStickerImage(
    drawableRes: Int?,
    fallbackEmoji: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    emojiSize: TextUnit = 24.sp
) {
    val context = LocalContext.current
    val bitmap = remember(drawableRes) {
        if (drawableRes != null && drawableRes != 0) {
            try {
                BitmapFactory.decodeResource(context.resources, drawableRes)?.asImageBitmap()
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Text(
            text = fallbackEmoji,
            fontSize = emojiSize
        )
    }
}

@Composable
fun StickerView(
    stickerId: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    onClick: (() -> Unit)? = null
) {
    val sticker = StickerPacks.cozyItems.find { it.id == stickerId }

    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, RoundedCornerShape(12.dp), ambientColor = Color.Black.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(2.5.dp, Color.White, RoundedCornerShape(12.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        CozyStickerImage(
            drawableRes = sticker?.drawableRes,
            fallbackEmoji = sticker?.emoji ?: "💮",
            contentDescription = sticker?.name,
            modifier = Modifier.size(size - 8.dp),
            emojiSize = (size.value * 0.55f).sp
        )
    }
}
