package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JournalEntity
import com.example.ui.components.PaperCard
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
fun JournalScreen(
    currentEntry: JournalEntity?,
    onSaveJournal: (date: String, mood: String, body: String) -> Unit,
    onBack: () -> Unit
) {
    val cottons = LocalCottons.current
    val context = LocalContext.current

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val displayDate = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date()) }

    var mood by remember { mutableStateOf(currentEntry?.mood ?: "Cozy") }
    var bodyText by remember { mutableStateOf(currentEntry?.body ?: "") }

    val moodOptions = listOf(
        "Cozy" to "☕",
        "Happy" to "☀️",
        "Inspired" to "✨",
        "Calm" to "🌿",
        "Tired" to "🌙"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = cottons.ink)
            }
            Text(
                text = "Daily Journal",
                fontFamily = SerifFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = cottons.ink
            )
        }

        Spacer(Modifier.height(12.dp))

        PaperCard(
            modifier = Modifier.fillMaxWidth(),
            style = PaperStyle.LINED_CREAM,
            tape = true,
            hasHoles = true,
            elevation = 4.dp
        ) {
            Column {
                Text(
                    text = displayDate,
                    fontFamily = MonoFont,
                    fontSize = 12.sp,
                    color = cottons.inkSoft
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Gentle Reflection",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = cottons.ink
                )

                Spacer(Modifier.height(14.dp))

                // Mood Sticker Picker
                Text(
                    text = "Today's Mood Sticker:",
                    fontFamily = SansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moodOptions.forEach { (name, emoji) ->
                        val isSel = mood == name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { mood = name }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(if (isSel) 3.dp else 1.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(if (isSel) cottons.paperAlt else Color(0xFFF9F7F2))
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSel) cottons.primary else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontFamily = HandFont,
                                fontSize = 11.sp,
                                color = if (isSel) cottons.primary else cottons.inkSoft
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Daily Prompt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cottons.paperAlt.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Prompt: \"What brought you gentle joy or peace today?\"",
                        fontFamily = HandFont,
                        fontSize = 15.sp,
                        color = cottons.ink
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Journal text body
                OutlinedTextField(
                    value = bodyText,
                    onValueChange = { bodyText = it },
                    placeholder = {
                        Text(
                            text = "Write freely... tea steeping, gentle words, ideas that bloomed today...",
                            fontFamily = HandFont,
                            fontSize = 16.sp,
                            color = cottons.inkSoft.copy(alpha = 0.5f)
                        )
                    },
                    minLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cottons.primary,
                        unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.25f),
                        focusedTextColor = cottons.ink,
                        unfocusedTextColor = cottons.ink
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                // Save & Seal Button
                Button(
                    onClick = {
                        onSaveJournal(todayStr, mood, bodyText)
                        Toast.makeText(context, "Journal page sealed in wax! 💮", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = cottons.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💮", fontSize = 16.sp)
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Save & Apply Wax Seal",
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
