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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IdeaEntity
import com.example.ui.components.PaperCard
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaPocketSheet(
    ideas: List<IdeaEntity>,
    onDismiss: () -> Unit,
    onAddIdea: (text: String, color: String) -> Unit,
    onConvertToTask: (IdeaEntity) -> Unit,
    onDeleteIdea: (IdeaEntity) -> Unit
) {
    val cottons = LocalCottons.current
    var newIdeaText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("YELLOW") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_CREAM,
                tape = true,
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 22.sp)
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "Idea Pocket",
                                fontFamily = SerifFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = cottons.ink
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = cottons.inkSoft)
                        }
                    }

                    Text(
                        text = "Quickly tuck away spontaneous thoughts, sparks, and inspirations",
                        fontFamily = HandFont,
                        fontSize = 14.sp,
                        color = cottons.inkSoft
                    )

                    Spacer(Modifier.height(12.dp))

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newIdeaText,
                            onValueChange = { newIdeaText = it },
                            placeholder = { Text("Catch a new thought...", fontFamily = HandFont) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cottons.primary,
                                unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                                focusedTextColor = cottons.ink,
                                unfocusedTextColor = cottons.ink
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.size(8.dp))

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(3.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(cottons.primary)
                                .clickable {
                                    if (newIdeaText.isNotBlank()) {
                                        onAddIdea(newIdeaText.trim(), selectedColor)
                                        newIdeaText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Ideas list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ideas, key = { it.id }) { idea ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(10.dp))
                                    .background(
                                        when (idea.color) {
                                            "PINK" -> Color(0xFFFDEEF2)
                                            "CREAM" -> Color(0xFFFFFDF5)
                                            else -> Color(0xFFFFF6D6)
                                        },
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(1.dp, Color(0xFFE8DCC0), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = idea.text,
                                        fontFamily = HandFont,
                                        fontSize = 15.sp,
                                        color = cottons.ink,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row {
                                        // Convert to task button
                                        IconButton(onClick = { onConvertToTask(idea) }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Convert to Task",
                                                tint = cottons.primary
                                            )
                                        }

                                        IconButton(onClick = { onDeleteIdea(idea) }) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete Idea",
                                                tint = cottons.inkSoft
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
    }
}
