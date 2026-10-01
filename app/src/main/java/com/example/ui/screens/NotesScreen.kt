package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.ResponsiveContainer
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont

@Composable
fun NotesScreen(
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onOpenTask: (TaskEntity) -> Unit,
    onAddNewTask: () -> Unit,
    onDeleteTask: (String) -> Unit
) {
    val cottons = LocalCottons.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }

    val filters = listOf("All", "Pending", "Done", "High Priority")

    val filteredTasks = tasks.filter { task ->
        val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
            task.notes.contains(searchQuery, ignoreCase = true) ||
            task.tags.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Pending" -> !task.isDone
            "Done" -> task.isDone
            "High Priority" -> task.priority >= 3
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ResponsiveContainer(maxWidth = 840.dp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 76.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Screen title and Grid / List toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notes & Tasks",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = cottons.ink
                        )
                        Text(
                            text = "${tasks.count { !it.isDone }} open notes pinned to desk",
                            fontFamily = HandFont,
                            fontSize = 15.sp,
                            color = cottons.inkSoft
                        )
                    }

                    // Toggle View mode button
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(cottons.paper, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = "Toggle Grid",
                            tint = cottons.ink
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search paper notes...",
                            fontFamily = HandFont,
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = cottons.inkSoft
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cottons.primary,
                        unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                        focusedContainerColor = cottons.paper,
                        unfocusedContainerColor = cottons.paper,
                        focusedTextColor = cottons.ink,
                        unfocusedTextColor = cottons.ink
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                )

                Spacer(Modifier.height(10.dp))

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .sizeIn(minHeight = 44.dp)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) cottons.primary else cottons.paper)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) cottons.primary else cottons.inkSoft.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                fontFamily = HandFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else cottons.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // List or Adaptive Grid of notes
                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        PaperCard(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            style = PaperStyle.LINED_CREAM,
                            tape = true
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("🐱", fontSize = 42.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No notes match '$searchQuery'" else "Your desk is clear!",
                                    fontFamily = SerifFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = cottons.ink
                                )
                                Text(
                                    text = "Tap + to pin a new paper note.",
                                    fontFamily = HandFont,
                                    fontSize = 15.sp,
                                    color = cottons.inkSoft
                                )
                            }
                        }
                    }
                } else if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            NoteCardItem(
                                task = task,
                                isGrid = true,
                                onToggle = { onToggleTask(task) },
                                onClick = { onOpenTask(task) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            NoteCardItem(
                                task = task,
                                isGrid = false,
                                onToggle = { onToggleTask(task) },
                                onClick = { onOpenTask(task) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Sticky Note FAB (positioned safely above nav insets)
        FloatingActionButton(
            onClick = onAddNewTask,
            containerColor = cottons.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .testTag("notes_fab")
        ) {
            Row(
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "New Note",
                    fontFamily = HandFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun NoteCardItem(
    task: TaskEntity,
    isGrid: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val cottons = LocalCottons.current

    val style = when (task.paper) {
        "LINED_PINK" -> PaperStyle.LINED_PINK
        "LINED_BLUE" -> PaperStyle.LINED_BLUE
        "KRAFT" -> PaperStyle.KRAFT
        else -> PaperStyle.LINED_CREAM
    }

    PaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        style = style,
        hasHoles = !isGrid,
        tape = isGrid,
        elevation = 3.dp,
        rotation = if (isGrid) ((task.id.hashCode() % 3).toFloat()) else 0f
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Hand-drawn Checkbox with min 48dp touch target
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

                Row {
                    repeat(task.priority) {
                        Text("⭐", fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = task.title,
                fontFamily = SerifFont,
                fontWeight = FontWeight.Bold,
                fontSize = if (isGrid) 15.sp else 16.sp,
                color = if (task.isDone) cottons.inkSoft else cottons.ink,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (task.notes.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.notes,
                    fontFamily = HandFont,
                    fontSize = 13.sp,
                    color = cottons.inkSoft,
                    maxLines = if (isGrid) 2 else 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.estimateMin != null) {
                    Text(
                        text = "⏱ ${task.estimateMin}m",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = cottons.primary
                    )
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                if (task.tags.isNotBlank()) {
                    Text(
                        text = "#${task.tags}",
                        fontFamily = HandFont,
                        fontSize = 12.sp,
                        color = cottons.inkSoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
