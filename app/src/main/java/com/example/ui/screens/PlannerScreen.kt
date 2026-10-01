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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.TaskEntity
import com.example.ui.components.ReceiptCard
import com.example.ui.components.ReceiptDivider
import com.example.ui.components.ResponsiveContainer
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.SerifFont
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PlannerScreen(
    tasks: List<TaskEntity>,
    onScheduleTask: (TaskEntity, Long, Long) -> Unit,
    onStartFocus: (taskTitle: String?, durationMin: Int) -> Unit
) {
    val cottons = LocalCottons.current

    val daysOfWeek = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val numFormat = SimpleDateFormat("d", Locale.getDefault())
        repeat(7) {
            list.add(dayFormat.format(cal.time) to numFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_WEEK, 1)
        }
        list
    }

    var selectedDayIndex by remember { mutableIntStateOf(0) }

    val timeSlots = listOf(
        "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM",
        "11:00 AM", "12:00 PM", "01:00 PM", "02:00 PM",
        "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM",
        "07:00 PM", "08:00 PM", "09:00 PM"
    )

    ResponsiveContainer(maxWidth = 720.dp) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Receipt Timeline",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = cottons.ink
                )
                Text(
                    text = "Time-box your cozy day into uninterrupted focus blocks",
                    fontFamily = HandFont,
                    fontSize = 15.sp,
                    color = cottons.inkSoft
                )
            }

            // Week strip of stamp dates
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysOfWeek.size) { index ->
                        val (dayName, dayNum) = daysOfWeek[index]
                        val isSelected = index == selectedDayIndex

                        Box(
                            modifier = Modifier
                                .sizeIn(minWidth = 52.dp, minHeight = 48.dp)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) cottons.primary else cottons.paper)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) cottons.primary else cottons.inkSoft.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedDayIndex = index }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayName,
                                    fontFamily = MonoFont,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else cottons.inkSoft
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = dayNum,
                                    fontFamily = SerifFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color.White else cottons.ink
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Receipt Card
            item {
                ReceiptCard(
                    receiptNo = "DAY PLAN · " + daysOfWeek.getOrNull(selectedDayIndex)?.first.orEmpty()
                ) {
                    timeSlots.forEach { time ->
                        val matchedTask = when (time) {
                            "07:00 AM" -> tasks.find { it.title.contains("Workout", true) }
                            "09:00 AM" -> tasks.find { it.title.contains("UI", true) }
                            "10:00 AM" -> tasks.find { it.title.contains("YouTube", true) }
                            "01:00 PM" -> tasks.find { it.title.contains("Cafe", true) }
                            "08:00 PM" -> tasks.find { it.title.contains("Read", true) }
                            else -> null
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = time,
                                fontFamily = MonoFont,
                                fontSize = 12.sp,
                                color = cottons.inkSoft,
                                modifier = Modifier.width(76.dp)
                            )

                            if (matchedTask != null) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(2.dp, RoundedCornerShape(8.dp))
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(cottons.paperAlt)
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = matchedTask.title,
                                                fontFamily = SerifFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = cottons.ink,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Focus Block • ${matchedTask.estimateMin ?: 45} mins",
                                                fontFamily = HandFont,
                                                fontSize = 12.sp,
                                                color = cottons.primary
                                            )
                                        }

                                        Spacer(Modifier.width(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(cottons.primary)
                                                .clickable {
                                                    onStartFocus(matchedTask.title, matchedTask.estimateMin ?: 45)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PlayArrow,
                                                contentDescription = "Start Focus Block",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(cottons.paper.copy(alpha = 0.5f))
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "- open desk slot -",
                                        fontFamily = MonoFont,
                                        fontSize = 11.sp,
                                        color = cottons.inkSoft.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                        ReceiptDivider()
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
