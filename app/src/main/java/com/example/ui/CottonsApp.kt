package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.AppContainer
import com.example.model.AmbientSound
import com.example.model.TaskEntity
import com.example.ui.components.CottonsBackground
import com.example.ui.components.CottonsTab
import com.example.ui.components.LocalWindowWidthClass
import com.example.ui.components.TinCaseNavBar
import com.example.ui.components.TinCaseNavRail
import com.example.ui.components.WindowWidthClass
import com.example.ui.components.rememberWindowWidthClass
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.IdeaPocketSheet
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.NoteEditorSheet
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.CottonsFontFamily
import com.example.ui.theme.CottonsTheme
import com.example.ui.theme.CottonsThemes
import kotlinx.coroutines.launch

enum class SubScreen {
    NONE, FOCUS, HABITS, INSIGHTS, SETTINGS, JOURNAL
}

@Composable
fun CottonsApp(container: AppContainer) {
    val prefs = container.prefs
    val repo = container.repository
    val scope = rememberCoroutineScope()
    val widthClass = rememberWindowWidthClass()

    val isOnboarded by prefs.onboarded.collectAsState()
    val themeName by prefs.themeName.collectAsState()
    val userName by prefs.userName.collectAsState()
    val soundEnabled by prefs.soundEnabled.collectAsState()
    val hapticsEnabled by prefs.hapticsEnabled.collectAsState()
    val darkMode by prefs.darkMode.collectAsState()
    val fontChoice by prefs.fontChoice.collectAsState()
    val customAudioPath by prefs.customAudioPath.collectAsState()

    val currentTheme = CottonsThemes.map[themeName] ?: CottonsThemes.PicnicRed
    val currentFontFamily = remember(fontChoice) {
        CottonsFontFamily.entries.find { it.id == fontChoice } ?: CottonsFontFamily.CURSIVE
    }

    val tasks by repo.allTasks.collectAsState(initial = emptyList())
    val focusSessions by repo.focusSessions.collectAsState(initial = emptyList())
    val habits by repo.allHabits.collectAsState(initial = emptyList())
    val habitLogs by repo.allHabitLogs.collectAsState(initial = emptyList())
    val sealsCount by repo.sealsCount.collectAsState(initial = 1)
    val ideas by repo.allIdeas.collectAsState(initial = emptyList())
    val journalEntries by repo.journalEntries.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(CottonsTab.TODAY) }
    var activeSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var focusTaskTitle by remember { mutableStateOf<String?>("Edit YouTube Video") }
    var focusDurationMin by remember { mutableStateOf(25) }

    // Bottom Sheet states
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var showNoteEditor by remember { mutableStateOf(false) }
    var showIdeaPocket by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalWindowWidthClass provides widthClass) {
        CottonsTheme(
            colors = currentTheme,
            darkMode = darkMode,
            fontFamily = currentFontFamily
        ) {
            if (!isOnboarded) {
                OnboardingScreen(
                    currentTheme = currentTheme,
                    onFinish = { name, chosenTheme ->
                        prefs.setUserName(name)
                        prefs.setTheme(chosenTheme)
                        prefs.setOnboarded(true)
                    }
                )
            } else {
                CottonsBackground(
                    colors = currentTheme,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isCompact = widthClass == WindowWidthClass.COMPACT

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        // Adaptive Navigation Rail on Medium / Expanded screens
                        if (!isCompact && activeSubScreen == SubScreen.NONE) {
                            TinCaseNavRail(
                                selectedTab = selectedTab,
                                onTabSelected = { tab ->
                                    container.sounds.playTapeClick()
                                    if (tab == CottonsTab.MORE) {
                                        activeSubScreen = SubScreen.SETTINGS
                                    } else {
                                        selectedTab = tab
                                    }
                                }
                            )
                        }

                        // Main Content Scaffold
                        Scaffold(
                            containerColor = Color.Transparent,
                            bottomBar = {
                                if (isCompact && activeSubScreen == SubScreen.NONE) {
                                    TinCaseNavBar(
                                        selectedTab = selectedTab,
                                        onTabSelected = { tab ->
                                            container.sounds.playTapeClick()
                                            if (tab == CottonsTab.MORE) {
                                                activeSubScreen = SubScreen.SETTINGS
                                            } else {
                                                selectedTab = tab
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                if (activeSubScreen != SubScreen.NONE) {
                                    BackHandler {
                                        activeSubScreen = SubScreen.NONE
                                        container.ambientPlayer.stop()
                                    }
                                }

                                when (activeSubScreen) {
                                    SubScreen.FOCUS -> {
                                        FocusScreen(
                                            initialTaskTitle = focusTaskTitle,
                                            initialMinutes = focusDurationMin,
                                            tasks = tasks,
                                            onCompleteSession = { title, plannedSec, actualSec, stars ->
                                                scope.launch {
                                                    repo.logFocusSession(
                                                        taskId = null,
                                                        taskTitle = title,
                                                        plannedSec = plannedSec,
                                                        actualSec = actualSec,
                                                        completed = true,
                                                        tape = "${plannedSec / 60}m",
                                                        stars = stars
                                                    )
                                                }
                                            },
                                            onAmbientSoundChange = { sound ->
                                                if (sound == AmbientSound.LOFI && customAudioPath != null) {
                                                    container.ambientPlayer.playCustomAudio(customAudioPath!!)
                                                } else {
                                                    container.ambientPlayer.play(sound)
                                                }
                                            },
                                            onPlayTapeClickSound = {
                                                container.sounds.playTapeClick()
                                            }
                                        )
                                    }
                                    SubScreen.HABITS -> {
                                        HabitsScreen(
                                            habits = habits,
                                            logs = habitLogs,
                                            onToggleHabit = { habitId, date, currentlyDone ->
                                                scope.launch {
                                                    repo.toggleHabitLog(habitId, date, currentlyDone)
                                                }
                                            },
                                            onAddHabit = { name, icon, stampStyle ->
                                                scope.launch {
                                                    repo.addHabit(name, icon, stampStyle)
                                                }
                                            },
                                            onPlayStampSound = {
                                                container.sounds.playStampThunk()
                                            }
                                        )
                                    }
                                    SubScreen.INSIGHTS -> {
                                        InsightsScreen(
                                            tasks = tasks,
                                            sessions = focusSessions,
                                            sealsCount = sealsCount,
                                            onCloseDayClick = {
                                                container.sounds.playWaxSealSquish()
                                                scope.launch {
                                                    repo.awardStamp("SEAL", "Daily Wax Seal Close")
                                                }
                                            }
                                        )
                                    }
                                    SubScreen.SETTINGS -> {
                                        SettingsScreen(
                                            currentTheme = currentTheme,
                                            userName = userName,
                                            darkMode = darkMode,
                                            fontChoice = fontChoice,
                                            soundEnabled = soundEnabled,
                                            hapticsEnabled = hapticsEnabled,
                                            tasks = tasks,
                                            sessions = focusSessions,
                                            habits = habits,
                                            habitLogs = habitLogs,
                                            onThemeSelect = { prefs.setTheme(it) },
                                            onNameChange = { prefs.setUserName(it) },
                                            onDarkModeChange = { prefs.setDarkMode(it) },
                                            onFontChoiceChange = { prefs.setFontChoice(it) },
                                            onSoundToggle = { prefs.setSoundEnabled(it) },
                                            onHapticsToggle = { prefs.setHapticsEnabled(it) }
                                        )
                                    }
                                    SubScreen.JOURNAL -> {
                                        JournalScreen(
                                            currentEntry = journalEntries.firstOrNull(),
                                            onSaveJournal = { date, mood, body ->
                                                container.sounds.playWaxSealSquish()
                                                scope.launch {
                                                    repo.saveJournal(
                                                        com.example.model.JournalEntity(
                                                            date = date,
                                                            mood = mood,
                                                            body = body
                                                        )
                                                    )
                                                }
                                                activeSubScreen = SubScreen.NONE
                                            },
                                            onBack = { activeSubScreen = SubScreen.NONE }
                                        )
                                    }
                                    SubScreen.NONE -> {
                                        when (selectedTab) {
                                            CottonsTab.TODAY -> {
                                                TodayScreen(
                                                    userName = userName,
                                                    tasks = tasks,
                                                    completedSessionsToday = focusSessions.count { it.completed },
                                                    sealsCount = sealsCount,
                                                    onToggleTask = { task ->
                                                        container.sounds.playPaperRustle()
                                                        scope.launch { repo.toggleTaskDone(task) }
                                                    },
                                                    onOpenTask = { task ->
                                                        editingTask = task
                                                        showNoteEditor = true
                                                    },
                                                    onStartFocus = { title, duration ->
                                                        focusTaskTitle = title
                                                        focusDurationMin = duration
                                                        activeSubScreen = SubScreen.FOCUS
                                                    },
                                                    onAddNoteClick = {
                                                        editingTask = null
                                                        showNoteEditor = true
                                                    },
                                                    onIdeaPocketClick = { showIdeaPocket = true },
                                                    onSeeAllTasksClick = { selectedTab = CottonsTab.NOTES }
                                                )
                                            }
                                            CottonsTab.NOTES -> {
                                                NotesScreen(
                                                    tasks = tasks,
                                                    onToggleTask = { task ->
                                                        container.sounds.playPaperRustle()
                                                        scope.launch { repo.toggleTaskDone(task) }
                                                    },
                                                    onOpenTask = { task ->
                                                        editingTask = task
                                                        showNoteEditor = true
                                                    },
                                                    onAddNewTask = {
                                                        editingTask = null
                                                        showNoteEditor = true
                                                    },
                                                    onDeleteTask = { id ->
                                                        scope.launch { repo.deleteTask(id) }
                                                    }
                                                )
                                            }
                                            CottonsTab.PLANNER -> {
                                                PlannerScreen(
                                                    tasks = tasks,
                                                    onScheduleTask = { task, start, end ->
                                                        scope.launch { repo.scheduleTask(task, start, end) }
                                                    },
                                                    onStartFocus = { title, duration ->
                                                        focusTaskTitle = title
                                                        focusDurationMin = duration
                                                        activeSubScreen = SubScreen.FOCUS
                                                    }
                                                )
                                            }
                                            CottonsTab.STUDIO -> {
                                                StudioScreen(
                                                    currentBoard = null,
                                                    onSaveBoard = {
                                                        container.sounds.playWaxSealSquish()
                                                    },
                                                    onOpenJournal = { activeSubScreen = SubScreen.JOURNAL },
                                                    onOpenIdeaPocket = { showIdeaPocket = true },
                                                    onCustomAudioImported = { path ->
                                                        prefs.setCustomAudio(path)
                                                        container.ambientPlayer.playCustomAudio(path)
                                                    }
                                                )
                                            }
                                            CottonsTab.MORE -> {
                                                // Handled by activeSubScreen = SubScreen.SETTINGS
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Note Editor Sheet Modal
                    if (showNoteEditor) {
                        NoteEditorSheet(
                            task = editingTask,
                            onDismiss = { showNoteEditor = false },
                            onSave = { title, notes, estimateMin, priority, paper, tags ->
                                container.sounds.playPaperRustle()
                                scope.launch {
                                    if (editingTask != null) {
                                        repo.updateTask(
                                            editingTask!!.copy(
                                                title = title,
                                                notes = notes,
                                                estimateMin = estimateMin,
                                                priority = priority,
                                                paper = paper,
                                                tags = tags
                                            )
                                        )
                                    } else {
                                        repo.addTask(
                                            title = title,
                                            notes = notes,
                                            estimateMin = estimateMin,
                                            priority = priority,
                                            paper = paper,
                                            tags = tags
                                        )
                                    }
                                }
                            },
                            onDelete = {
                                if (editingTask != null) {
                                    scope.launch { repo.deleteTask(editingTask!!.id) }
                                    showNoteEditor = false
                                }
                            }
                        )
                    }

                    // Idea Pocket Sheet Modal
                    if (showIdeaPocket) {
                        IdeaPocketSheet(
                            ideas = ideas,
                            onDismiss = { showIdeaPocket = false },
                            onAddIdea = { text, color ->
                                container.sounds.playPaperRustle()
                                scope.launch { repo.addIdea(text, color) }
                            },
                            onConvertToTask = { idea ->
                                container.sounds.playStampThunk()
                                scope.launch { repo.convertIdeaToTask(idea) }
                            },
                            onDeleteIdea = { idea ->
                                scope.launch { repo.deleteIdea(idea) }
                            }
                        )
                    }
                }
            }
        }
    }
}
