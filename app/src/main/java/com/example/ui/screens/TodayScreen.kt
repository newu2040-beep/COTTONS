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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.ResponsiveContainer
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    userName: String,
    tasks: List<TaskEntity>,
    completedSessionsToday: Int,
    sealsCount: Int,
    onToggleTask: (TaskEntity) -> Unit,
    onOpenTask: (TaskEntity) -> Unit,
    onStartFocus: (taskTitle: String?, durationMin: Int) -> Unit,
    onAddNoteClick: () -> Unit,
    onIdeaPocketClick: () -> Unit,
    onSeeAllTasksClick: () -> Unit
) {
    val cottons = LocalCottons.current

    val todayFormatted = remember {
        val sdf = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    val doneTasksCount = tasks.count { it.isDone }
    val isSealEarned = doneTasksCount >= 3 && completedSessionsToday >= 1

    val nextBlockTask = tasks.firstOrNull { it.blockStart != null && !it.isDone }
        ?: tasks.firstOrNull { !it.isDone }

    ResponsiveContainer(maxWidth = 840.dp) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                // Header: Greeting, Date, and "Make today count!" sticky note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good Morning",
                            fontFamily = SerifFont,
                            fontSize = 16.sp,
                            color = cottons.inkSoft
                        )
                        Text(
                            text = "$userName ♥",
                            fontFamily = HandFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = cottons.ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = todayFormatted,
                            fontFamily = SansFont,
                            fontSize = 13.sp,
                            color = cottons.inkSoft
                        )
                    }

                    // Pinned "Make today count!" tiny paper note
                    Box(
                        modifier = Modifier
                            .rotate(4f)
                            .shadow(3.dp, RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFF4D4), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFFEBD399), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Make\ntoday\ncount !",
                            fontFamily = HandFont,
                            fontSize = 13.sp,
                            color = Color(0xFF6B4B1B),
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Daily Progress & Wax Seal Milestone Strip
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = PaperStyle.PLAIN,
                    elevation = 2.dp,
                    rotation = 0f
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DailyMilestoneItem(
                            icon = "✓",
                            isCompleted = doneTasksCount >= 3,
                            label = "$doneTasksCount/3 Tasks"
                        )
                        DailyMilestoneItem(
                            icon = "📼",
                            isCompleted = completedSessionsToday >= 1,
                            label = "$completedSessionsToday Focus Tape"
                        )
                        DailyMilestoneItem(
                            icon = "💮",
                            isCompleted = isSealEarned,
                            label = if (isSealEarned) "Wax Seal Earned" else "Seal at 3+1"
                        )
                    }
                }
            }

            // Next Focus Block Spotlight
            if (nextBlockTask != null) {
                item {
                    Column {
                        Text(
                            text = "Up Next on Desk",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = cottons.ink,
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )

                        PaperCard(
                            modifier = Modifier.fillMaxWidth(),
                            style = PaperStyle.LINED_CREAM,
                            tape = true,
                            tapeRotation = 3f,
                            rotation = -0.5f,
                            hasHoles = true,
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .shadow(2.dp, RoundedCornerShape(8.dp))
                                            .background(cottons.paperAlt, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💻", fontSize = 22.sp)
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "10:00 - 11:00 AM",
                                            fontFamily = MonoFont,
                                            fontSize = 11.sp,
                                            color = cottons.inkSoft
                                        )
                                        Text(
                                            text = nextBlockTask.title,
                                            fontFamily = SerifFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = cottons.ink,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Focus • ${nextBlockTask.estimateMin ?: 25} min",
                                            fontFamily = HandFont,
                                            fontSize = 14.sp,
                                            color = cottons.primary
                                        )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                        .size(46.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(cottons.primary)
                                        .clickable {
                                            onStartFocus(nextBlockTask.title, nextBlockTask.estimateMin ?: 25)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Start Focus",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Today's Tasks Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Tasks",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "See all →",
                        fontFamily = HandFont,
                        fontSize = 15.sp,
                        color = cottons.primary,
                        modifier = Modifier.clickable { onSeeAllTasksClick() }
                    )
                }
            }

            // Empty state or Task list
            if (tasks.isEmpty()) {
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
                            Text("📝", fontSize = 36.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Your desk is clear for today",
                                fontFamily = SerifFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = cottons.ink
                            )
                            Text(
                                text = "Tap '+ Add Note' below to write your first plan ♡",
                                fontFamily = HandFont,
                                fontSize = 14.sp,
                                color = cottons.inkSoft
                            )
                        }
                    }
                }
            } else {
                items(tasks.take(6), key = { it.id }) { task ->
                    TodayTaskRow(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onClick = { onOpenTask(task) }
                    )
                }
            }

            // Quick Actions Row
            item {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        label = "Add Note",
                        icon = "+",
                        bgColor = Color(0xFFFBE7EA),
                        textColor = cottons.primary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_note_button"),
                        onClick = onAddNoteClick
                    )

                    QuickActionButton(
                        label = "Start Focus",
                        icon = "📼",
                        bgColor = Color(0xFFE8EEF8),
                        textColor = Color(0xFF14285A),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("start_focus_button"),
                        onClick = { onStartFocus(null, 25) }
                    )

                    QuickActionButton(
                        label = "Idea Pocket",
                        icon = "💡",
                        bgColor = Color(0xFFFFF4D4),
                        textColor = Color(0xFF6B4B1B),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("idea_pocket_button"),
                        onClick = onIdeaPocketClick
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DailyMilestoneItem(
    icon: String,
    isCompleted: Boolean,
    label: String
) {
    val cottons = LocalCottons.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(if (isCompleted) 2.dp else 0.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isCompleted) cottons.primary else cottons.paperAlt)
                .border(
                    width = 1.dp,
                    color = if (isCompleted) cottons.primary else cottons.inkSoft.copy(alpha = 0.25f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                color = if (isCompleted) Color.White else cottons.inkSoft
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = MonoFont,
            fontSize = 11.sp,
            color = if (isCompleted) cottons.ink else cottons.inkSoft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TodayTaskRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val cottons = LocalCottons.current
    val paperStyle = when (task.paper) {
        "LINED_PINK" -> PaperStyle.LINED_PINK
        "LINED_BLUE" -> PaperStyle.LINED_BLUE
        "KRAFT" -> PaperStyle.KRAFT
        else -> PaperStyle.LINED_CREAM
    }

    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        style = paperStyle,
        hasHoles = true,
        elevation = 2.dp,
        rotation = if (task.priority == 3) -0.5f else 0f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Checkbox with safe >= 48dp touch target
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (task.isDone) cottons.secondary else Color.Transparent)
                            .border(
                                width = 1.8.dp,
                                color = if (task.isDone) cottons.secondary else cottons.inkSoft.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isDone) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Done",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(6.dp))

                val catEmoji = when {
                    task.title.contains("workout", true) || task.title.contains("gym", true) -> "🏋️"
                    task.title.contains("design", true) || task.title.contains("ui", true) -> "🎨"
                    task.title.contains("cafe", true) || task.title.contains("coffee", true) -> "☕"
                    task.title.contains("read", true) || task.title.contains("book", true) -> "📖"
                    task.title.contains("video", true) || task.title.contains("youtube", true) -> "📹"
                    else -> "📝"
                }
                Text(text = catEmoji, fontSize = 18.sp)

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontFamily = SansFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (task.isDone) cottons.inkSoft else cottons.ink,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (task.estimateMin != null) "* ${task.estimateMin} mins" else "Anytime",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = cottons.inkSoft
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Priority stars
            Row {
                repeat(task.priority) {
                    Text("⭐", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .sizeIn(minHeight = 48.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = HandFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
