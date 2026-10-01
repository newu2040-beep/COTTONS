package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AmbientSound
import com.example.model.TaskEntity
import com.example.ui.components.CassettePlayer
import com.example.ui.components.PaperCard
import com.example.ui.components.ResponsiveContainer
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont
import kotlinx.coroutines.delay

@Composable
fun FocusScreen(
    initialTaskTitle: String?,
    initialMinutes: Int = 25,
    tasks: List<TaskEntity>,
    onCompleteSession: (taskTitle: String?, plannedSec: Int, actualSec: Int, stars: Int) -> Unit,
    onAmbientSoundChange: (AmbientSound) -> Unit,
    onPlayTapeClickSound: () -> Unit
) {
    val cottons = LocalCottons.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedTaskTitle by remember { mutableStateOf(initialTaskTitle ?: "Deep Work Focus") }
    var plannedMinutes by remember { mutableIntStateOf(initialMinutes) }
    var remainingSeconds by remember { mutableIntStateOf(plannedMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var currentSound by remember { mutableStateOf(AmbientSound.CAFE) }
    var showReflectionDialog by remember { mutableStateOf(false) }
    var reflectionStars by remember { mutableIntStateOf(3) }

    // Timer countdown loop
    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
            if (remainingSeconds <= 0) {
                isRunning = false
                showReflectionDialog = true
            }
        }
    }

    if (isLandscape) {
        // Landscape Mode: 2 equal scrollable panes
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Header + Cassette Player
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus Cassette",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "Session 1/4",
                        fontFamily = MonoFont,
                        fontSize = 12.sp,
                        color = cottons.inkSoft
                    )
                }

                Spacer(Modifier.height(10.dp))

                CassettePlayer(
                    taskTitle = selectedTaskTitle,
                    remainingSec = remainingSeconds,
                    plannedSec = plannedMinutes * 60,
                    isRunning = isRunning,
                    onTogglePlay = {
                        onPlayTapeClickSound()
                        isRunning = !isRunning
                    },
                    onSkipBack = {
                        onPlayTapeClickSound()
                        isRunning = false
                        remainingSeconds = plannedMinutes * 60
                    },
                    onSkipForward = {
                        onPlayTapeClickSound()
                        isRunning = false
                        showReflectionDialog = true
                    }
                )
            }

            // Right Pane: Presets + Ambient Sounds + Encouragement
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FocusPresetsRow(
                    plannedMinutes = plannedMinutes,
                    isRunning = isRunning,
                    onSelectPreset = { mins ->
                        plannedMinutes = mins
                        remainingSeconds = mins * 60
                        onPlayTapeClickSound()
                    }
                )

                Spacer(Modifier.height(14.dp))

                AmbientSoundsSection(
                    currentSound = currentSound,
                    onSelectSound = { sound ->
                        currentSound = if (currentSound == sound) AmbientSound.NONE else sound
                        onAmbientSoundChange(currentSound)
                    }
                )

                Spacer(Modifier.height(14.dp))

                EncouragementNote()
            }
        }
    } else {
        // Portrait Mode: Responsive centered single-column
        ResponsiveContainer(maxWidth = 600.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus Cassette",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "Session 1/4",
                        fontFamily = MonoFont,
                        fontSize = 13.sp,
                        color = cottons.inkSoft
                    )
                }

                Spacer(Modifier.height(14.dp))

                CassettePlayer(
                    taskTitle = selectedTaskTitle,
                    remainingSec = remainingSeconds,
                    plannedSec = plannedMinutes * 60,
                    isRunning = isRunning,
                    onTogglePlay = {
                        onPlayTapeClickSound()
                        isRunning = !isRunning
                    },
                    onSkipBack = {
                        onPlayTapeClickSound()
                        isRunning = false
                        remainingSeconds = plannedMinutes * 60
                    },
                    onSkipForward = {
                        onPlayTapeClickSound()
                        isRunning = false
                        showReflectionDialog = true
                    }
                )

                Spacer(Modifier.height(18.dp))

                FocusPresetsRow(
                    plannedMinutes = plannedMinutes,
                    isRunning = isRunning,
                    onSelectPreset = { mins ->
                        plannedMinutes = mins
                        remainingSeconds = mins * 60
                        onPlayTapeClickSound()
                    }
                )

                Spacer(Modifier.height(18.dp))

                AmbientSoundsSection(
                    currentSound = currentSound,
                    onSelectSound = { sound ->
                        currentSound = if (currentSound == sound) AmbientSound.NONE else sound
                        onAmbientSoundChange(currentSound)
                    }
                )

                Spacer(Modifier.height(18.dp))

                EncouragementNote()

                Spacer(Modifier.height(28.dp))
            }
        }
    }

    // Celebration & Reflection Dialog
    if (showReflectionDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showReflectionDialog = false }) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(0.92f),
                style = PaperStyle.LINED_CREAM,
                tape = true,
                elevation = 8.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    WaxSealBadge(size = 58.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Focus Complete! 💮",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "You earned a cassette tape stamp.",
                        fontFamily = HandFont,
                        fontSize = 16.sp,
                        color = cottons.inkSoft
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "How was your session?",
                        fontFamily = SansFont,
                        fontSize = 14.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Row {
                        for (i in 1..3) {
                            Text(
                                text = if (i <= reflectionStars) "⭐" else "☆",
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .clickable { reflectionStars = i }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(minHeight = 48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cottons.primary)
                            .clickable {
                                onCompleteSession(
                                    selectedTaskTitle,
                                    plannedMinutes * 60,
                                    plannedMinutes * 60 - remainingSeconds,
                                    reflectionStars
                                )
                                showReflectionDialog = false
                                remainingSeconds = plannedMinutes * 60
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save & Stamp Journal",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusPresetsRow(
    plannedMinutes: Int,
    isRunning: Boolean,
    onSelectPreset: (Int) -> Unit
) {
    val cottons = LocalCottons.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val presets = listOf(
            "25/5" to 25,
            "50/10" to 50,
            "90/20" to 90,
            "Custom" to 15
        )
        presets.forEach { (label, mins) ->
            val isSelected = plannedMinutes == mins
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 54.dp, minHeight = 48.dp)
                    .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) cottons.primary else cottons.paper)
                    .border(
                        1.dp,
                        if (isSelected) cottons.primary else cottons.inkSoft.copy(alpha = 0.25f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        if (!isRunning) {
                            onSelectPreset(mins)
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = MonoFont,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else cottons.ink
                )
            }
        }
    }
}

@Composable
private fun AmbientSoundsSection(
    currentSound: AmbientSound,
    onSelectSound: (AmbientSound) -> Unit
) {
    val cottons = LocalCottons.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Ambient Sounds",
            fontFamily = SerifFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = cottons.ink
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AmbientSound.entries.filter { it != AmbientSound.NONE }.forEach { sound ->
                val isSelected = currentSound == sound
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable { onSelectSound(sound) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) cottons.paperAlt else cottons.paper)
                            .border(
                                1.5.dp,
                                if (isSelected) cottons.primary else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = sound.icon, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = sound.label,
                        fontFamily = HandFont,
                        fontSize = 11.sp,
                        color = if (isSelected) cottons.primary else cottons.inkSoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun EncouragementNote() {
    val cottons = LocalCottons.current
    PaperCard(
        modifier = Modifier.fillMaxWidth(),
        style = PaperStyle.LINED_CREAM,
        elevation = 3.dp,
        rotation = -1f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Stay consistent",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = cottons.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "You're doing great! Small moments add up ♥",
                    fontFamily = HandFont,
                    fontSize = 13.sp,
                    color = cottons.inkSoft,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            com.example.ui.components.CozyStickerImage(
                drawableRes = R.drawable.sticker_cat,
                fallbackEmoji = "🐱",
                contentDescription = "Cute Cat",
                modifier = Modifier.size(44.dp),
                emojiSize = 30.sp
            )
        }
    }
}
