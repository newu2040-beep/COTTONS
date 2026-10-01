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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FocusSessionEntity
import com.example.model.TaskEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.ReceiptCard
import com.example.ui.components.ReceiptDivider
import com.example.ui.components.ReceiptItemRow
import com.example.ui.components.ResponsiveContainer
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SerifFont

@Composable
fun InsightsScreen(
    tasks: List<TaskEntity>,
    sessions: List<FocusSessionEntity>,
    sealsCount: Int,
    onCloseDayClick: () -> Unit
) {
    val cottons = LocalCottons.current

    val totalFocusSeconds = sessions.sumOf { it.actualSec }
    val focusHours = totalFocusSeconds / 3600
    val focusMins = (totalFocusSeconds % 3600) / 60
    val completedTasksCount = tasks.count { it.isDone }

    val weeklyTally = remember {
        listOf(
            "Mon" to 45,
            "Tue" to 75,
            "Wed" to 50,
            "Thu" to 90,
            "Fri" to 60,
            "Sat" to 25,
            "Sun" to 40
        )
    }

    ResponsiveContainer(maxWidth = 640.dp) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Café Receipt & Insights",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = cottons.ink
                )
                Text(
                    text = "Your quiet hours, small wins, and weekly momentum",
                    fontFamily = HandFont,
                    fontSize = 15.sp,
                    color = cottons.inkSoft
                )
            }

            // Receipt Card Summary
            item {
                ReceiptCard(
                    receiptNo = "RECEIPT #0042 · WEEKLY SUMMARY"
                ) {
                    ReceiptItemRow("FOCUS TIME", "${focusHours}h ${focusMins}m")
                    ReceiptItemRow("TASKS COMPLETED", "$completedTasksCount")
                    ReceiptItemRow("CURRENT STREAK", "6 DAYS")
                    ReceiptItemRow("WAX SEALS EARNED", "$sealsCount SEALS")
                    ReceiptItemRow("FAVORITE SOUND", "CAFÉ LO-FI")

                    ReceiptDivider(Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "FOCUS TIME TALLY (MINUTES)",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyTally.forEach { (day, mins) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontFamily = MonoFont,
                                    fontSize = 9.sp,
                                    color = cottons.inkSoft
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(width = 22.dp, height = (mins * 0.9f).dp.coerceIn(12.dp, 80.dp))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            if (day == "Thu") cottons.primary else cottons.secondary.copy(alpha = 0.6f)
                                        )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    fontFamily = MonoFont,
                                    fontSize = 10.sp,
                                    color = cottons.ink
                                )
                            }
                        }
                    }
                }
            }

            // Daily Close Wax Seal Card
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = PaperStyle.LINED_CREAM,
                    tape = true,
                    elevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Close Ritual",
                                fontFamily = SerifFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = cottons.ink
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Review your finished notes, celebrate deep work, and stamp your daily wax seal.",
                                fontFamily = HandFont,
                                fontSize = 14.sp,
                                color = cottons.inkSoft,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .sizeIn(minWidth = 56.dp, minHeight = 56.dp)
                                .clickable { onCloseDayClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            WaxSealBadge(size = 54.dp)
                        }
                    }
                }
            }

            // Milestone Badges Collection
            item {
                PaperCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = PaperStyle.KRAFT,
                    elevation = 2.dp
                ) {
                    Text(
                        text = "Unlocked Stationery Seals",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(
                            Triple("7-Day Streak", "🔥", true),
                            Triple("10 Hours", "⌛", true),
                            Triple("Scrap Master", "✂️", true),
                            Triple("Night Owl", "🌙", false)
                        ).forEach { (title, badge, isUnlocked) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .shadow(if (isUnlocked) 3.dp else 0.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(if (isUnlocked) Color(0xFFC8283C) else Color(0xFFC4B8A8))
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 20.sp,
                                        color = if (isUnlocked) Color.White else Color.Gray
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    fontFamily = HandFont,
                                    fontSize = 11.sp,
                                    color = if (isUnlocked) cottons.ink else cottons.inkSoft,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
