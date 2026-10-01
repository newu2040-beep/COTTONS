package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.SerifFont

@Composable
fun CassettePlayer(
    taskTitle: String,
    remainingSec: Int,
    plannedSec: Int,
    isRunning: Boolean,
    onTogglePlay: () -> Unit,
    onSkipBack: () -> Unit,
    onSkipForward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cottons = LocalCottons.current

    val infiniteTransition = rememberInfiniteTransition(label = "reels")
    val reelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reelSpin"
    )

    val currentRotation = if (isRunning) reelRotation else 0f
    val progress = (1f - (remainingSec.toFloat() / plannedSec.coerceAtLeast(1).toFloat())).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        val availableWidth = maxWidth
        val timerFontSize = (availableWidth.value * 0.14f).coerceIn(34f, 56f).sp

        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cassette Tape Shell (scaled keeping aspect ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.58f)
                    .shadow(10.dp, RoundedCornerShape(20.dp), ambientColor = cottons.shadow)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                cottons.tertiary,
                                cottons.paperAlt,
                                cottons.secondary.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp)
            ) {
                // Screws in 4 corners
                Screw(Modifier.align(Alignment.TopStart).padding(4.dp))
                Screw(Modifier.align(Alignment.TopEnd).padding(4.dp))
                Screw(Modifier.align(Alignment.BottomStart).padding(4.dp))
                Screw(Modifier.align(Alignment.BottomEnd).padding(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Label Area (lined paper sticker)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(34.dp)
                            .shadow(2.dp, RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFFDF7), RoundedCornerShape(6.dp))
                            .border(1.dp, cottons.inkSoft.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = taskTitle.ifBlank { "Deep Work Focus Session" } + " ♥",
                            fontFamily = HandFont,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = cottons.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Transparent Tape Window with dual spools
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .height(60.dp)
                            .shadow(2.dp, RoundedCornerShape(10.dp))
                            .background(Color(0xFF22262B), RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color(0xFF4A515A), RoundedCornerShape(10.dp))
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Tape ribbon between reels
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val midY = size.height / 2f
                            drawLine(
                                color = Color(0xFF5B3929),
                                start = Offset(size.width * 0.28f, midY),
                                end = Offset(size.width * 0.72f, midY),
                                strokeWidth = 8.dp.toPx()
                            )
                        }

                        // Left & Right spools
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TapeReel(
                                angle = currentRotation,
                                tapeRadiusFactor = 1f - (progress * 0.45f)
                            )
                            TapeReel(
                                angle = currentRotation,
                                tapeRadiusFactor = 0.55f + (progress * 0.45f)
                            )
                        }
                    }

                    // Bottom cassette notch cut
                    Row(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        repeat(4) {
                            Box(
                                Modifier
                                    .size(8.dp, 5.dp)
                                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Decorative bear sticker
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp)
                        .size(26.dp)
                        .background(Color(0xFFFFFDF8), CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧸", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Timer Digits Display with responsive font size
            val minutes = remainingSec / 60
            val seconds = remainingSec % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            Text(
                text = timeString,
                fontFamily = SerifFont,
                fontSize = timerFontSize,
                fontWeight = FontWeight.Bold,
                color = cottons.ink,
                letterSpacing = 1.sp,
                maxLines = 1
            )

            Text(
                text = if (isRunning) "Focusing Deeply..." else if (remainingSec == 0) "Session Complete! 💮" else "Ready to Focus",
                fontFamily = HandFont,
                fontSize = 16.sp,
                color = cottons.inkSoft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // Progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = cottons.seal,
                trackColor = cottons.paperAlt.copy(alpha = 0.5f)
            )

            Spacer(Modifier.height(16.dp))

            // Hardware Cassette Keys Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous / Reset
                ChunkyKey(
                    onClick = onSkipBack,
                    size = 52.dp
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Restart Timer",
                        tint = cottons.ink,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(20.dp))

                // Main Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(8.dp, CircleShape, ambientColor = cottons.shadow)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    cottons.secondary,
                                    cottons.primary,
                                    cottons.primary.copy(red = cottons.primary.red * 0.7f)
                                )
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        .clickable { onTogglePlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.width(20.dp))

                // Next / Complete
                ChunkyKey(
                    onClick = onSkipForward,
                    size = 52.dp
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Skip Forward",
                        tint = cottons.ink,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TapeReel(angle: Float, tapeRadiusFactor: Float) {
    Canvas(
        modifier = Modifier
            .size(46.dp)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.width / 2f

        // Tape wound circle
        drawCircle(
            color = Color(0xFF6B4226),
            radius = (maxRadius * tapeRadiusFactor).coerceAtLeast(14.dp.toPx()),
            center = center
        )

        // White plastic hub
        drawCircle(
            color = Color(0xFFEFEFEF),
            radius = 12.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color(0xFFD4D4D4),
            radius = 12.dp.toPx(),
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Center hub hole
        drawCircle(
            color = Color(0xFF1E2228),
            radius = 5.dp.toPx(),
            center = center
        )

        // 3 spoke teeth that rotate
        rotate(angle, pivot = center) {
            val spokeLen = 8.dp.toPx()
            for (i in 0..2) {
                val spokeAngle = Math.toRadians((i * 120.0))
                val sx = center.x + (Math.cos(spokeAngle) * spokeLen).toFloat()
                val sy = center.y + (Math.sin(spokeAngle) * spokeLen).toFloat()
                drawCircle(
                    color = Color(0xFF6A727D),
                    radius = 2.dp.toPx(),
                    center = Offset(sx, sy)
                )
            }
        }
    }
}

@Composable
private fun ChunkyKey(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 52.dp,
    content: @Composable () -> Unit
) {
    val cottons = LocalCottons.current
    Box(
        modifier = Modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .size(size)
            .shadow(4.dp, RoundedCornerShape(14.dp), ambientColor = cottons.shadow)
            .clip(RoundedCornerShape(14.dp))
            .background(cottons.paper)
            .border(2.dp, Color.White, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun Screw(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFFE0E0E0))
            .border(0.5.dp, Color(0xFF9E9E9E), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(5.dp)
                .height(1.dp)
                .background(Color(0xFF757575))
        )
    }
}
