package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AssetImportHelper
import com.example.model.BoardEntity
import com.example.ui.components.PaperCard
import com.example.ui.components.StickerPacks
import com.example.ui.components.StickerView
import com.example.ui.components.WashiTape
import com.example.ui.components.WaxSealBadge
import com.example.ui.components.gingham
import com.example.ui.components.kraft
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SerifFont
import java.util.UUID
import kotlin.math.roundToInt

data class CanvasItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String, // STICKER, TEXT, TAPE, POLAROID, WAX_SEAL, PHOTO, CUSTOM_STICKER
    val payload: String,
    var x: Float,
    var y: Float,
    var scale: Float = 1.0f,
    var rotation: Float = 0f
)

@Composable
fun StudioScreen(
    currentBoard: BoardEntity?,
    onSaveBoard: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenIdeaPocket: () -> Unit,
    onCustomAudioImported: (String) -> Unit
) {
    val cottons = LocalCottons.current
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("Stickers") } // Stickers, Tape, Paper, Text, Import, BG
    var canvasBg by remember { mutableStateOf("GINGHAM") } // GINGHAM, KRAFT, GRID

    // Canvas items
    val items = remember {
        mutableStateListOf(
            CanvasItem(type = "POLAROID", payload = "Dream Café in Itahari ♥", x = 160f, y = 300f, scale = 1.0f, rotation = -2f),
            CanvasItem(type = "STICKER", payload = "cat", x = 100f, y = 840f, scale = 1.2f, rotation = -6f),
            CanvasItem(type = "STICKER", payload = "cherry", x = 420f, y = 160f, scale = 1.1f, rotation = 8f),
            CanvasItem(type = "STICKER", payload = "bow", x = 80f, y = 120f, scale = 1.0f, rotation = -5f),
            CanvasItem(type = "STICKER", payload = "tulip", x = 100f, y = 1020f, scale = 1.1f, rotation = 4f),
            CanvasItem(type = "WAX_SEAL", payload = "seal", x = 520f, y = 560f, scale = 1.0f, rotation = 0f),
            CanvasItem(type = "TAPE", payload = "rose", x = 200f, y = 220f, scale = 1.0f, rotation = -4f),
            CanvasItem(type = "TEXT", payload = "note to self:\n• be proud\n• keep going\n• good things take time ♡", x = 400f, y = 680f, scale = 1.0f, rotation = 2f)
        )
    }

    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var showTextDialog by remember { mutableStateOf(false) }

    // Pickers for Import Picture, Custom Sticker, Audio
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = AssetImportHelper.saveImportedPhoto(context, uri)
            if (savedPath != null) {
                items.add(
                    CanvasItem(
                        type = "PHOTO",
                        payload = savedPath,
                        x = 180f,
                        y = 350f,
                        scale = 1.0f,
                        rotation = 2f
                    )
                )
                Toast.makeText(context, "Photo imported onto scrapbook! 📸", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val stickerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = AssetImportHelper.saveCustomSticker(context, uri)
            if (savedPath != null) {
                items.add(
                    CanvasItem(
                        type = "CUSTOM_STICKER",
                        payload = savedPath,
                        x = 220f,
                        y = 400f,
                        scale = 1.0f,
                        rotation = 0f
                    )
                )
                Toast.makeText(context, "Custom sticker added! ✨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = AssetImportHelper.saveImportedAudio(context, uri)
            if (savedPath != null) {
                onCustomAudioImported(savedPath)
                Toast.makeText(context, "Cassette tape audio imported! 🎵", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Scrapbook Studio",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = cottons.ink
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (items.isNotEmpty()) items.removeAt(items.lastIndex)
                }) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = cottons.ink)
                }

                Spacer(Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(cottons.paperAlt)
                        .clickable { onOpenJournal() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = "📖 Journal", fontFamily = HandFont, fontSize = 12.sp, color = cottons.primary)
                }

                Spacer(Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(cottons.primary)
                        .clickable {
                            Toast.makeText(context, "Scrapbook Board Saved! 💮", Toast.LENGTH_SHORT).show()
                            onSaveBoard()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Save",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Main Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(
                    when (canvasBg) {
                        "KRAFT" -> Modifier.kraft(Color(0xFFEADBCE))
                        "GRID" -> Modifier.background(cottons.paper)
                        else -> Modifier.gingham(cottons.checkA.copy(alpha = 0.5f), cottons.background)
                    }
                )
        ) {
            items.forEach { item ->
                CanvasItemView(
                    item = item,
                    isSelected = selectedItemId == item.id,
                    onClick = { selectedItemId = item.id },
                    onDelete = { items.remove(item) }
                )
            }
        }

        // Bottom Tray Dock
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(cottons.paper)
                .border(1.dp, cottons.inkSoft.copy(alpha = 0.2f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(12.dp)
        ) {
            // Dock tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val dockTabs = listOf("Stickers", "Tape", "Paper", "Text", "Import", "BG")
                dockTabs.forEach { tab ->
                    val isSel = tab == selectedTab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                selectedTab = tab
                                if (tab == "Text") showTextDialog = true
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        val icon = when (tab) {
                            "Stickers" -> "🌸"
                            "Tape" -> "🩹"
                            "Paper" -> "📜"
                            "Text" -> "✍️"
                            "Import" -> "📥"
                            else -> "🎨"
                        }
                        Text(text = icon, fontSize = 18.sp)
                        Text(
                            text = tab,
                            fontFamily = HandFont,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) cottons.primary else cottons.inkSoft
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            when (selectedTab) {
                "Stickers" -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(StickerPacks.cozyItems) { sticker ->
                            StickerView(
                                stickerId = sticker.id,
                                size = 48.dp,
                                onClick = {
                                    items.add(
                                        CanvasItem(
                                            type = "STICKER",
                                            payload = sticker.id,
                                            x = 200f + (items.size * 20f % 260f),
                                            y = 350f + (items.size * 20f % 260f),
                                            rotation = (items.size * 7 % 20 - 10).toFloat()
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                "Tape" -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val tapeColors = listOf(
                            cottons.tape to "Gold Tape",
                            Color(0xFFF7C6CB) to "Blush Tape",
                            Color(0xFFB7CCE8) to "Sky Tape",
                            Color(0xFFDCE6B8) to "Sage Tape"
                        )
                        items(tapeColors) { (color, label) ->
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        items.add(
                                            CanvasItem(
                                                type = "TAPE",
                                                payload = label,
                                                x = 220f,
                                                y = 450f,
                                                rotation = -6f
                                            )
                                        )
                                    }
                                    .padding(4.dp)
                            ) {
                                WashiTape(color = color, width = 70.dp, height = 22.dp)
                            }
                        }
                    }
                }
                "Paper" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("LINED_CREAM" to "Cream", "LINED_PINK" to "Blush", "LINED_BLUE" to "Denim", "KRAFT" to "Kraft").forEach { (style, name) ->
                            Button(
                                onClick = {
                                    items.add(
                                        CanvasItem(
                                            type = "PAPER",
                                            payload = "A cozy note on $name paper ♥",
                                            x = 200f,
                                            y = 450f
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = cottons.paperAlt),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(name, fontFamily = HandFont, color = cottons.ink, fontSize = 13.sp)
                            }
                        }
                    }
                }
                "Import" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = cottons.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("📷 Picture", fontFamily = SerifFont, fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                stickerPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = cottons.secondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("✨ Sticker", fontFamily = SerifFont, fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                audioPickerLauncher.launch("audio/*")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = cottons.paperAlt),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🎵 Audio", fontFamily = SerifFont, fontSize = 12.sp, color = cottons.ink)
                        }
                    }
                }
                "BG" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("GINGHAM" to "Gingham", "KRAFT" to "Kraft", "GRID" to "Paper Grid").forEach { (bg, label) ->
                            Button(
                                onClick = { canvasBg = bg },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canvasBg == bg) cottons.primary else cottons.paperAlt
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    label,
                                    fontFamily = HandFont,
                                    color = if (canvasBg == bg) Color.White else cottons.ink
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTextDialog) {
        AddTextItemDialog(
            onDismiss = { showTextDialog = false },
            onConfirm = { text ->
                items.add(
                    CanvasItem(
                        type = "TEXT",
                        payload = text,
                        x = 220f,
                        y = 480f
                    )
                )
                showTextDialog = false
            }
        )
    }
}

@Composable
private fun CanvasItemView(
    item: CanvasItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(item.x) }
    var offsetY by remember { mutableFloatStateOf(item.y) }
    var scale by remember { mutableFloatStateOf(item.scale) }
    var rotation by remember { mutableFloatStateOf(item.rotation) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .pointerInput(item.id) {
                detectTransformGestures { _, pan, zoom, rot ->
                    offsetX += pan.x
                    offsetY += pan.y
                    scale = (scale * zoom).coerceIn(0.5f, 3.0f)
                    rotation += rot
                    item.x = offsetX
                    item.y = offsetY
                    item.scale = scale
                    item.rotation = rotation
                }
            }
            .clickable { onClick() }
    ) {
        when (item.type) {
            "STICKER" -> {
                StickerView(stickerId = item.payload, size = 68.dp)
            }
            "CUSTOM_STICKER" -> {
                val bitmap = remember(item.payload) {
                    try {
                        BitmapFactory.decodeFile(item.payload)?.asImageBitmap()
                    } catch (_: Exception) { null }
                }
                if (bitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Custom Sticker",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    StickerView(stickerId = "star", size = 68.dp)
                }
            }
            "WAX_SEAL" -> {
                WaxSealBadge(size = 62.dp)
            }
            "TAPE" -> {
                WashiTape(width = 88.dp, height = 26.dp)
            }
            "PHOTO" -> {
                val bitmap = remember(item.payload) {
                    try {
                        BitmapFactory.decodeFile(item.payload)?.asImageBitmap()
                    } catch (_: Exception) { null }
                }
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .shadow(6.dp, RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFFDF8), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFE2DDD2), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Imported Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFDCE6DE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📷", fontSize = 32.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Imported Memory ♥",
                            fontFamily = HandFont,
                            fontSize = 13.sp,
                            color = Color(0xFF3B1A1F)
                        )
                    }
                }
            }
            "POLAROID" -> {
                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .shadow(6.dp, RoundedCornerShape(4.dp))
                        .background(Color(0xFFFFFDF8), RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFFE2DDD2), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFDCE6DE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("☕ 🌿 🏠", fontSize = 28.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = item.payload,
                            fontFamily = HandFont,
                            fontSize = 13.sp,
                            color = Color(0xFF3B1A1F)
                        )
                    }
                }
            }
            "TEXT", "PAPER" -> {
                PaperCard(
                    modifier = Modifier.width(180.dp),
                    style = PaperStyle.LINED_CREAM,
                    elevation = 4.dp
                ) {
                    Text(
                        text = item.payload,
                        fontFamily = HandFont,
                        fontSize = 14.sp,
                        color = Color(0xFF3B1A1F),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC8283C))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun AddTextItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        PaperCard(
            modifier = Modifier.fillMaxWidth(0.95f),
            style = PaperStyle.LINED_CREAM,
            tape = true
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Add Handwritten Script",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Write an affirmation or scrap note...", fontFamily = HandFont) },
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) onConfirm(text.trim())
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pin onto Board", fontFamily = SerifFont)
                }
            }
        }
    }
}
