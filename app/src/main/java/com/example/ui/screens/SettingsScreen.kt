package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.data.DataExportManager
import com.example.model.FocusSessionEntity
import com.example.model.HabitEntity
import com.example.model.HabitLogEntity
import com.example.model.TaskEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.CottonsColors
import com.example.ui.theme.CottonsFontFamily
import com.example.ui.theme.CottonsThemes
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont

@Composable
fun SettingsScreen(
    currentTheme: CottonsColors,
    userName: String,
    darkMode: String,
    fontChoice: String,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    tasks: List<TaskEntity>,
    sessions: List<FocusSessionEntity>,
    habits: List<HabitEntity>,
    habitLogs: List<HabitLogEntity>,
    onThemeSelect: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDarkModeChange: (String) -> Unit,
    onFontChoiceChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onHapticsToggle: (Boolean) -> Unit
) {
    val cottons = LocalCottons.current
    val context = LocalContext.current

    var showAboutDialog by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(userName) }

    // Notification Permission Status
    var isNotificationGranted by remember {
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            true
        }
        mutableStateOf(granted)
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isNotificationGranted = granted
        if (granted) {
            Toast.makeText(context, "Notifications enabled! Focus countdowns will notify smoothly.", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tin Case Settings",
                fontFamily = SerifFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = cottons.ink
            )
            Text(
                text = "Personalize your stationery desk, themes, exports, and audio",
                fontFamily = HandFont,
                fontSize = 15.sp,
                color = cottons.inkSoft
            )
        }

        // Dark Mode / Light Mode Switch
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_CREAM,
                elevation = 3.dp
            ) {
                Text(
                    text = "Display Mode",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val modes = listOf(
                        "LIGHT" to "☀️ Light",
                        "DARK" to "🌙 Dark",
                        "SYSTEM" to "⚙️ System"
                    )
                    modes.forEach { (modeKey, label) ->
                        val isSelected = darkMode == modeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) cottons.primary else cottons.paperAlt.copy(alpha = 0.5f))
                                .clickable { onDarkModeChange(modeKey) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontFamily = SerifFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else cottons.ink
                            )
                        }
                    }
                }
            }
        }

        // Built-in Font Selector
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_BLUE,
                elevation = 2.dp
            ) {
                Text(
                    text = "Built-in Typography Style",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CottonsFontFamily.entries.forEach { fontOption ->
                        val isSelected = fontChoice == fontOption.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) cottons.paperAlt else Color.Transparent)
                                .clickable { onFontChoiceChange(fontOption.id) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fontOption.label,
                                fontFamily = fontOption.font,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = cottons.ink
                            )
                            if (isSelected) {
                                Text("✓ Active", fontFamily = HandFont, fontSize = 14.sp, color = cottons.primary)
                            }
                        }
                    }
                }
            }
        }

        // Permissions Status & Notification Access
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_PINK,
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notification Access",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = cottons.ink
                        )
                        Text(
                            text = if (isNotificationGranted) "Enabled ✓ Alerts for timer & blocks" else "Grant notification permission for timer",
                            fontFamily = HandFont,
                            fontSize = 13.sp,
                            color = if (isNotificationGranted) cottons.primary else Color(0xFFC8283C)
                        )
                    }

                    if (!isNotificationGranted) {
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = cottons.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Allow", fontFamily = SerifFont, color = Color.White, fontSize = 12.sp)
                        }
                    } else {
                        Text("🔔 Granted", fontFamily = MonoFont, fontSize = 12.sp, color = cottons.primary)
                    }
                }
            }
        }

        // Cute Stationery Themes
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_CREAM,
                elevation = 3.dp
            ) {
                Text(
                    text = "Cute Stationery Themes",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = cottons.ink
                )
                Text(
                    text = "Choose from 12 aesthetic fabric & craft paper palettes",
                    fontFamily = HandFont,
                    fontSize = 13.sp,
                    color = cottons.inkSoft
                )
                Spacer(Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CottonsThemes.all.forEach { themeItem ->
                        val isSelected = themeItem.name == currentTheme.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) cottons.paperAlt.copy(alpha = 0.6f) else Color.Transparent)
                                .clickable { onThemeSelect(themeItem.name) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .shadow(2.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(themeItem.primary)
                                        .border(2.dp, Color.White, CircleShape)
                                )
                                Spacer(Modifier.size(10.dp))
                                Column {
                                    Text(
                                        text = themeItem.name,
                                        fontFamily = SerifFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = cottons.ink
                                    )
                                    Text(
                                        text = themeItem.pattern.name.lowercase().replaceFirstChar { it.uppercase() } + " Pattern",
                                        fontFamily = HandFont,
                                        fontSize = 11.sp,
                                        color = cottons.inkSoft
                                    )
                                }
                            }

                            if (isSelected) {
                                Text("💮 Active", fontFamily = HandFont, fontSize = 13.sp, color = cottons.primary)
                            }
                        }
                    }
                }
            }
        }

        // Export Data in PDF, CSV, TXT Formats Smoothly
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_CREAM,
                tape = true,
                elevation = 3.dp
            ) {
                Text(
                    text = "Export Planner & Journal Data",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cottons.ink
                )
                Text(
                    text = "Save or share your notes, focus logs, and habit history smoothly",
                    fontFamily = HandFont,
                    fontSize = 13.sp,
                    color = cottons.inkSoft
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export PDF
                    Button(
                        onClick = {
                            try {
                                val uri = DataExportManager.exportToPdf(context, tasks, sessions, habits, habitLogs)
                                DataExportManager.shareFile(context, uri, "application/pdf", "Share Cottons PDF Report")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cottons.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📄", fontSize = 18.sp)
                            Text("PDF File", fontFamily = SerifFont, fontSize = 12.sp, color = Color.White)
                        }
                    }

                    // Export CSV
                    Button(
                        onClick = {
                            try {
                                val uri = DataExportManager.exportToCsv(context, tasks, sessions, habits, habitLogs)
                                DataExportManager.shareFile(context, uri, "text/csv", "Share Cottons CSV Table")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error generating CSV: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cottons.secondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 18.sp)
                            Text("CSV Table", fontFamily = SerifFont, fontSize = 12.sp, color = Color.White)
                        }
                    }

                    // Export TXT
                    Button(
                        onClick = {
                            try {
                                val uri = DataExportManager.exportToTxt(context, tasks, sessions, habits, habitLogs)
                                DataExportManager.shareFile(context, uri, "text/plain", "Share Cottons TXT Receipt")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error generating TXT: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cottons.paperAlt),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 18.sp)
                            Text("TXT Receipt", fontFamily = SerifFont, fontSize = 12.sp, color = cottons.ink)
                        }
                    }
                }
            }
        }

        // Profile & Name
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_BLUE,
                elevation = 2.dp
            ) {
                Text(
                    text = "Journal Name",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = {
                            editingName = it
                            onNameChange(it)
                        },
                        singleLine = true,
                        placeholder = { Text("Your Name", fontFamily = HandFont) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cottons.primary,
                            unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.3f),
                            focusedTextColor = cottons.ink,
                            unfocusedTextColor = cottons.ink
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Tactile Haptics & Sounds
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.LINED_PINK,
                elevation = 2.dp
            ) {
                Text(
                    text = "Tactile & Haptics",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stationery Sounds",
                            fontFamily = SansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = cottons.ink
                        )
                        Text(
                            text = "Tape clicks, paper rustles & stamp thunks",
                            fontFamily = HandFont,
                            fontSize = 12.sp,
                            color = cottons.inkSoft
                        )
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = onSoundToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = cottons.primary, checkedTrackColor = cottons.paperAlt)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Haptic Feedback",
                            fontFamily = SansFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = cottons.ink
                        )
                        Text(
                            text = "Tactile vibration when pressing stamps",
                            fontFamily = HandFont,
                            fontSize = 12.sp,
                            color = cottons.inkSoft
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onHapticsToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = cottons.primary, checkedTrackColor = cottons.paperAlt)
                    )
                }
            }
        }

        // About dialog trigger
        item {
            PaperCard(
                modifier = Modifier.fillMaxWidth(),
                style = PaperStyle.KRAFT,
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAboutDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "💮 About COTTONS", fontFamily = SerifFont, fontSize = 15.sp, color = cottons.ink)
                    Text(text = "Credits & Info →", fontFamily = HandFont, fontSize = 14.sp, color = cottons.primary)
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }

    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(0.95f),
                style = PaperStyle.LINED_CREAM,
                tape = true,
                elevation = 6.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    WaxSealBadge(size = 56.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "COTTONS",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "Version 1.1 • Offline First",
                        fontFamily = HandFont,
                        fontSize = 14.sp,
                        color = cottons.inkSoft
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "\"A cozy paper-craft planner where your tasks are notes, your focus sessions are cassette tapes, your finished days earn wax seals, and your ideas live on a sticker board.\"",
                        fontFamily = SerifFont,
                        fontSize = 13.sp,
                        color = cottons.ink,
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Designed & Built for:",
                        fontFamily = SansFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = cottons.inkSoft
                    )
                    Text(
                        text = "Rahul Shah / Editingcells",
                        fontFamily = HandFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = cottons.primary
                    )
                }
            }
        }
    }
}
