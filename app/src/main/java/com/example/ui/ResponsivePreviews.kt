package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.model.TaskEntity
import com.example.ui.components.CassettePlayer
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.CottonsTheme
import com.example.ui.theme.CottonsThemes

private val sampleTasks = listOf(
    TaskEntity(
        title = "Edit YouTube Video with cozy stationary vibes",
        notes = "Add quiet café background audio and export in 4K",
        priority = 3,
        estimateMin = 45,
        paper = "LINED_CREAM",
        tags = "video,editing"
    ),
    TaskEntity(
        title = "Morning Coffee & Journaling",
        notes = "Reflect on weekly milestones and habit stamps",
        priority = 2,
        estimateMin = 20,
        paper = "LINED_PINK",
        tags = "routine"
    ),
    TaskEntity(
        title = "Review Notion Database",
        notes = "",
        priority = 1,
        estimateMin = 15,
        paper = "LINED_BLUE",
        tags = "work"
    )
)

@Preview(name = "1. Compact Phone - 320x568 (Smallest)", widthDp = 320, heightDp = 568, fontScale = 1.0f)
@Composable
fun PreviewCompactSmallest() {
    CottonsTheme(colors = CottonsThemes.PicnicRed) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TodayScreen(
                userName = "Rahul",
                tasks = sampleTasks,
                completedSessionsToday = 1,
                sealsCount = 3,
                onToggleTask = {},
                onOpenTask = {},
                onStartFocus = { _, _ -> },
                onAddNoteClick = {},
                onIdeaPocketClick = {},
                onSeeAllTasksClick = {}
            )
        }
    }
}

@Preview(name = "2. Compact Standard - 360x640 @ 1.3x Font", widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Composable
fun PreviewCompactStandardLargeFont() {
    CottonsTheme(colors = CottonsThemes.StrawberryMilk) {
        Surface(modifier = Modifier.fillMaxSize()) {
            TodayScreen(
                userName = "Rahul",
                tasks = sampleTasks,
                completedSessionsToday = 2,
                sealsCount = 4,
                onToggleTask = {},
                onOpenTask = {},
                onStartFocus = { _, _ -> },
                onAddNoteClick = {},
                onIdeaPocketClick = {},
                onSeeAllTasksClick = {}
            )
        }
    }
}

@Preview(name = "3. Modern Flagship - 411x891 @ 2.0x Font (Accessibility Max)", widthDp = 411, heightDp = 891, fontScale = 2.0f)
@Composable
fun PreviewModernPhoneMaxFont() {
    CottonsTheme(colors = CottonsThemes.MatchaLatte) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NotesScreen(
                tasks = sampleTasks,
                onToggleTask = {},
                onOpenTask = {},
                onAddNewTask = {},
                onDeleteTask = {}
            )
        }
    }
}

@Preview(name = "4. Foldable Unfolded - 600x960", widthDp = 600, heightDp = 960, fontScale = 1.0f)
@Composable
fun PreviewFoldableUnfolded() {
    CottonsTheme(colors = CottonsThemes.TeddyBrownie) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NotesScreen(
                tasks = sampleTasks,
                onToggleTask = {},
                onOpenTask = {},
                onAddNewTask = {},
                onDeleteTask = {}
            )
        }
    }
}

@Preview(name = "5. Tablet Expanded - 840x1280", widthDp = 840, heightDp = 1280, fontScale = 1.0f)
@Composable
fun PreviewTabletExpanded() {
    CottonsTheme(colors = CottonsThemes.MidnightStudy, darkMode = "DARK") {
        Surface(modifier = Modifier.fillMaxSize()) {
            TodayScreen(
                userName = "Rahul",
                tasks = sampleTasks,
                completedSessionsToday = 3,
                sealsCount = 7,
                onToggleTask = {},
                onOpenTask = {},
                onStartFocus = { _, _ -> },
                onAddNoteClick = {},
                onIdeaPocketClick = {},
                onSeeAllTasksClick = {}
            )
        }
    }
}

@Preview(name = "6. Landscape Focus 2-Pane - 800x400", widthDp = 800, heightDp = 400, fontScale = 1.0f)
@Composable
fun PreviewLandscapeFocus() {
    CottonsTheme(colors = CottonsThemes.BlueberryJam) {
        Surface(modifier = Modifier.fillMaxSize()) {
            FocusScreen(
                initialTaskTitle = "Edit YouTube Video",
                initialMinutes = 25,
                tasks = sampleTasks,
                onCompleteSession = { _, _, _, _ -> },
                onAmbientSoundChange = {},
                onPlayTapeClickSound = {}
            )
        }
    }
}
