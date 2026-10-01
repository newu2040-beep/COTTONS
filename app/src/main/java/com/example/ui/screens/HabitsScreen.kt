package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.HabitEntity
import com.example.model.HabitLogEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.ResponsiveContainer
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SerifFont
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitsScreen(
    habits: List<HabitEntity>,
    logs: List<HabitLogEntity>,
    onToggleHabit: (habitId: String, date: String, currentlyDone: Boolean) -> Unit,
    onAddHabit: (name: String, icon: String, stampStyle: String) -> Unit,
    onPlayStampSound: () -> Unit
) {
    val cottons = LocalCottons.current

    val past7Days = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayLabelSdf = SimpleDateFormat("EEE", Locale.getDefault())
        val dateNumSdf = SimpleDateFormat("d", Locale.getDefault())
        val list = mutableListOf<Triple<String, String, String>>()
        val cal = Calendar.getInstance()
        repeat(7) {
            val key = sdf.format(cal.time)
            val dayLabel = dayLabelSdf.format(cal.time)
            val dateNum = dateNumSdf.format(cal.time)
            list.add(Triple(key, dayLabel, dateNum))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        list.reversed()
    }

    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ResponsiveContainer(maxWidth = 840.dp) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Habit Stamp Book",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "Press a stamp into your paper grid for every habit fulfilled",
                        fontFamily = HandFont,
                        fontSize = 15.sp,
                        color = cottons.inkSoft
                    )
                }

                if (habits.isEmpty()) {
                    item {
                        PaperCard(
                            modifier = Modifier.fillMaxWidth(),
                            style = PaperStyle.LINED_CREAM,
                            elevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🌱", fontSize = 38.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Plant your first habit seed",
                                    fontFamily = SerifFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = cottons.ink
                                )
                                Text(
                                    text = "Tap the + button to create a daily ritual.",
                                    fontFamily = HandFont,
                                    fontSize = 14.sp,
                                    color = cottons.inkSoft
                                )
                            }
                        }
                    }
                } else {
                    items(habits, key = { it.id }) { habit ->
                        val habitLogs = logs.filter { it.habitId == habit.id }
                        val streak = habitLogs.size

                        PaperCard(
                            modifier = Modifier.fillMaxWidth(),
                            style = PaperStyle.LINED_CREAM,
                            elevation = 3.dp,
                            hasHoles = true
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = when (habit.icon.lowercase()) {
                                                "cherry" -> "🍒"
                                                "star" -> "⭐"
                                                "flower" -> "🌸"
                                                "water" -> "💧"
                                                "book" -> "📖"
                                                "tape" -> "📼"
                                                "sun" -> "☀️"
                                                else -> "💮"
                                            },
                                            fontSize = 22.sp
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = habit.name,
                                                fontFamily = SerifFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = cottons.ink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "$streak stamps collected",
                                                fontFamily = HandFont,
                                                fontSize = 13.sp,
                                                color = cottons.primary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .shadow(1.dp, RoundedCornerShape(8.dp))
                                            .background(cottons.paperAlt, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🔥 $streak d",
                                            fontFamily = MonoFont,
                                            fontSize = 11.sp,
                                            color = cottons.ink
                                        )
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Perforated stamp slot grid with safe touch targets
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    past7Days.forEach { (dateKey, dayLabel, dateNum) ->
                                        val isDone = habitLogs.any { it.date == dateKey }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            StampTile(
                                                icon = habit.icon,
                                                label = dateNum,
                                                isStamped = isDone,
                                                onClick = {
                                                    onPlayStampSound()
                                                    onToggleHabit(habit.id, dateKey, isDone)
                                                }
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = dayLabel,
                                                fontFamily = MonoFont,
                                                fontSize = 9.sp,
                                                color = cottons.inkSoft
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to add habit (positioned safely with navigationBarsPadding)
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = cottons.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Habit")
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, icon, stampStyle ->
                onAddHabit(name, icon, stampStyle)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StampTile(
    icon: String,
    label: String,
    isStamped: Boolean,
    onClick: () -> Unit
) {
    val cottons = LocalCottons.current
    val stampEmoji = when (icon.lowercase()) {
        "cherry" -> "🍒"
        "star" -> "⭐"
        "flower" -> "🌸"
        "water" -> "💧"
        "book" -> "📖"
        "tape" -> "📼"
        "sun" -> "☀️"
        else -> "💮"
    }

    // Min 48dp touch target
    Box(
        modifier = Modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(if (isStamped) 3.dp else 1.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(if (isStamped) cottons.secondary.copy(alpha = 0.85f) else cottons.paper)
                .border(
                    width = 1.dp,
                    color = if (isStamped) cottons.primary else cottons.inkSoft.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isStamped) {
                Text(text = stampEmoji, fontSize = 20.sp)
            } else {
                Text(
                    text = label,
                    fontFamily = MonoFont,
                    fontSize = 11.sp,
                    color = cottons.inkSoft.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, icon: String, stampStyle: String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("cherry") }
    val icons = listOf("cherry", "star", "flower", "water", "book", "tape", "sun")

    Dialog(onDismissRequest = onDismiss) {
        PaperCard(
            modifier = Modifier.fillMaxWidth(0.95f),
            style = PaperStyle.LINED_CREAM,
            tape = true
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "New Habit Stamp",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    placeholder = { Text("e.g. Read 15 pages, Drink water", fontFamily = HandFont) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                Text("Choose Stamp Icon:", fontFamily = SerifFont, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    icons.forEach { ic ->
                        val isSel = ic == selectedIcon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSel) Color(0xFFC8283C) else Color(0xFFEFF3F8))
                                .clickable { selectedIcon = ic },
                            contentAlignment = Alignment.Center
                        ) {
                            val emoji = when (ic) {
                                "cherry" -> "🍒"
                                "star" -> "⭐"
                                "flower" -> "🌸"
                                "water" -> "💧"
                                "book" -> "📖"
                                "tape" -> "📼"
                                "sun" -> "☀️"
                                else -> "💮"
                            }
                            Text(emoji, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFC8283C))
                        .clickable {
                            if (habitName.isNotBlank()) {
                                onConfirm(habitName.trim(), selectedIcon, "RED_INK")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Create Stamp Slot",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
