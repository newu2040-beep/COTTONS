package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskEntity
import com.example.ui.components.PaperCard
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorSheet(
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        notes: String,
        estimateMin: Int?,
        priority: Int,
        paper: String,
        tags: String
    ) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val cottons = LocalCottons.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(task?.title ?: "") }
    var notes by remember { mutableStateOf(task?.notes ?: "") }
    var estimateMin by remember { mutableIntStateOf(task?.estimateMin ?: 25) }
    var priority by remember { mutableIntStateOf(task?.priority ?: 1) }
    var paper by remember { mutableStateOf(task?.paper ?: "LINED_CREAM") }
    var tags by remember { mutableStateOf(task?.tags ?: "") }

    val paperOptions = listOf(
        "LINED_CREAM" to Color(0xFFFFFDF5),
        "LINED_PINK" to Color(0xFFFDF0F2),
        "LINED_BLUE" to Color(0xFFEFF4FB),
        "KRAFT" to Color(0xFFE8D7BE)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(16.dp)
        ) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = when (paper) {
                    "LINED_PINK" -> PaperStyle.LINED_PINK
                    "LINED_BLUE" -> PaperStyle.LINED_BLUE
                    "KRAFT" -> PaperStyle.KRAFT
                    else -> PaperStyle.LINED_CREAM
                },
                tape = true,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Row: Title & Close/Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (task == null) "Pin New Note" else "Edit Note",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = cottons.ink
                        )

                        Row {
                            if (task != null && onDelete != null) {
                                IconButton(onClick = onDelete) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFC8283C)
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = cottons.inkSoft
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = {
                            Text(
                                text = "What's on your mind?",
                                fontFamily = HandFont,
                                fontSize = 18.sp,
                                color = cottons.inkSoft.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cottons.primary,
                            unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                            focusedTextColor = cottons.ink,
                            unfocusedTextColor = cottons.ink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // Notes / Details Input
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = {
                            Text(
                                text = "Add steps, reflections, or cozy details...",
                                fontFamily = SansFont,
                                fontSize = 14.sp,
                                color = cottons.inkSoft.copy(alpha = 0.5f)
                            )
                        },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cottons.primary,
                            unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                            focusedTextColor = cottons.ink,
                            unfocusedTextColor = cottons.ink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    // Priority Stars Selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Priority Stars:",
                            fontFamily = SansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = cottons.ink
                        )
                        Row {
                            for (i in 1..3) {
                                val active = i <= priority
                                Text(
                                    text = if (active) "⭐" else "☆",
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .clickable { priority = i }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Duration Estimate Chips
                    Text(
                        text = "Focus Duration Estimate:",
                        fontFamily = SansFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(15, 25, 45, 60).forEach { mins ->
                            val isSel = estimateMin == mins
                            Box(
                                modifier = Modifier
                                    .shadow(if (isSel) 2.dp else 0.dp, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) cottons.primary else cottons.paperAlt.copy(alpha = 0.5f))
                                    .clickable { estimateMin = mins }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontFamily = MonoFont,
                                    fontSize = 13.sp,
                                    color = if (isSel) Color.White else cottons.ink
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Paper Color Swatches
                    Text(
                        text = "Paper Color:",
                        fontFamily = SansFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = cottons.ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        paperOptions.forEach { (key, color) ->
                            val isSel = paper == key
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .shadow(if (isSel) 3.dp else 1.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSel) 2.5.dp else 1.dp,
                                        color = if (isSel) cottons.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable { paper = key },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSel) {
                                    Text("✓", fontSize = 12.sp, color = cottons.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Tags Input
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        placeholder = {
                            Text(
                                text = "Tags (e.g. Content, Creative, Study)",
                                fontFamily = HandFont,
                                fontSize = 14.sp,
                                color = cottons.inkSoft.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cottons.primary,
                            unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                            focusedTextColor = cottons.ink,
                            unfocusedTextColor = cottons.ink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(18.dp))

                    // Save Button
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, notes, estimateMin, priority, paper, tags)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cottons.primary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (task == null) "Pin Note to Desk" else "Save Changes",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
