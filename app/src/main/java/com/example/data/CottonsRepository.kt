package com.example.data

import com.example.model.BoardEntity
import com.example.model.BoardItemEntity
import com.example.model.FocusSessionEntity
import com.example.model.HabitEntity
import com.example.model.HabitLogEntity
import com.example.model.IdeaEntity
import com.example.model.JournalEntity
import com.example.model.StampEntity
import com.example.model.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CottonsRepository(
    private val database: CottonsDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val taskDao = database.taskDao()
    private val focusDao = database.focusDao()
    private val habitDao = database.habitDao()
    private val stampDao = database.stampDao()
    private val boardDao = database.boardDao()
    private val journalDao = database.journalDao()
    private val ideaDao = database.ideaDao()

    init {
        scope.launch {
            seedInitialDataIfEmpty()
        }
    }

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val scheduledTasks: Flow<List<TaskEntity>> = taskDao.getScheduledTasks()

    suspend fun getTask(id: String): TaskEntity? = taskDao.getTaskById(id)

    suspend fun addTask(
        title: String,
        notes: String = "",
        dueAt: Long? = null,
        estimateMin: Int? = 25,
        priority: Int = 1,
        paper: String = "LINED_CREAM",
        tags: String = "",
        blockStart: Long? = null,
        blockEnd: Long? = null
    ) {
        val task = TaskEntity(
            title = title,
            notes = notes,
            dueAt = dueAt,
            estimateMin = estimateMin,
            priority = priority,
            paper = paper,
            tags = tags,
            blockStart = blockStart,
            blockEnd = blockEnd
        )
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun toggleTaskDone(task: TaskEntity): Boolean {
        val now = System.currentTimeMillis()
        val willBeDone = task.doneAt == null
        val updated = task.copy(
            doneAt = if (willBeDone) now else null,
            updatedAt = now
        )
        taskDao.updateTask(updated)
        if (willBeDone) {
            awardStamp("TASK", "Task Complete: ${task.title}")
        }
        return willBeDone
    }

    suspend fun deleteTask(id: String) = taskDao.deleteTaskById(id)

    suspend fun scheduleTask(task: TaskEntity, startMs: Long, endMs: Long) {
        taskDao.updateTask(task.copy(blockStart = startMs, blockEnd = endMs, updatedAt = System.currentTimeMillis()))
    }

    // Focus sessions
    val focusSessions: Flow<List<FocusSessionEntity>> = focusDao.getAllSessions()

    suspend fun logFocusSession(
        taskId: String?,
        taskTitle: String?,
        plannedSec: Int,
        actualSec: Int,
        completed: Boolean,
        tape: String,
        stars: Int = 3
    ) {
        val session = FocusSessionEntity(
            taskId = taskId,
            taskTitle = taskTitle,
            plannedSec = plannedSec,
            actualSec = actualSec,
            completed = completed,
            tape = tape,
            reflectionStars = stars
        )
        focusDao.insertSession(session)
        if (completed) {
            awardStamp("SESSION", "Focus Tape: $tape")
        }
    }

    // Habits
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = habitDao.getAllLogs()

    suspend fun addHabit(name: String, icon: String = "cherry", stampStyle: String = "STAR") {
        habitDao.insertHabit(
            HabitEntity(
                name = name,
                icon = icon,
                stampStyle = stampStyle
            )
        )
    }

    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    suspend fun toggleHabitLog(habitId: String, date: String, currentlyDone: Boolean) {
        if (currentlyDone) {
            habitDao.deleteLog(habitId, date)
        } else {
            habitDao.insertLog(HabitLogEntity(habitId = habitId, date = date, count = 1))
            awardStamp("HABIT", "Habit Stamp: $date")
        }
    }

    // Stamps
    val allStamps: Flow<List<StampEntity>> = stampDao.getAllStamps()
    val sealsCount: Flow<Int> = stampDao.getSealsCount()

    suspend fun awardStamp(kind: String, label: String) {
        stampDao.insertStamp(
            StampEntity(
                kind = kind,
                label = label,
                earnedAt = System.currentTimeMillis()
            )
        )
    }

    // Boards & Studio
    val allBoards: Flow<List<BoardEntity>> = boardDao.getAllBoards()

    fun getBoardItems(boardId: String): Flow<List<BoardItemEntity>> = boardDao.getBoardItems(boardId)

    suspend fun addBoard(title: String, bg: String = "GINGHAM"): String {
        val id = UUID.randomUUID().toString()
        boardDao.insertBoard(BoardEntity(id = id, title = title, bg = bg))
        return id
    }

    suspend fun addBoardItem(item: BoardItemEntity) = boardDao.insertBoardItem(item)

    suspend fun updateBoardItem(item: BoardItemEntity) = boardDao.insertBoardItem(item)

    suspend fun deleteBoardItem(item: BoardItemEntity) = boardDao.deleteBoardItem(item)

    suspend fun clearBoardItems(boardId: String) = boardDao.clearBoardItems(boardId)

    // Journal
    val journalEntries: Flow<List<JournalEntity>> = journalDao.getAllEntries()

    suspend fun getJournalForDate(date: String): JournalEntity? = journalDao.getEntryForDate(date)

    suspend fun saveJournal(entry: JournalEntity) = journalDao.insertEntry(entry)

    // Ideas
    val allIdeas: Flow<List<IdeaEntity>> = ideaDao.getAllIdeas()

    suspend fun addIdea(text: String, color: String = "YELLOW") {
        ideaDao.insertIdea(IdeaEntity(text = text, color = color))
    }

    suspend fun deleteIdea(idea: IdeaEntity) = ideaDao.deleteIdea(idea)

    suspend fun convertIdeaToTask(idea: IdeaEntity) {
        addTask(title = idea.text, notes = "From Idea Pocket", paper = "LINED_CREAM")
        deleteIdea(idea)
    }

    // Seed realistic cozy sample data matching the mockup
    private suspend fun seedInitialDataIfEmpty() {
        val existingTasks = taskDao.getAllTasks().first()
        if (existingTasks.isNotEmpty()) return

        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Set up Next Block for today 10:00 - 11:00 AM
        cal.set(Calendar.HOUR_OF_DAY, 10)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val blockStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 11)
        val blockEnd = cal.timeInMillis

        // Tasks
        taskDao.insertTask(
            TaskEntity(
                title = "Edit YouTube Video",
                notes = "Cut B-roll footage, add cozy lo-fi audio, color grade scrap clips",
                estimateMin = 60,
                priority = 3,
                paper = "LINED_CREAM",
                tags = "Content, Creative",
                blockStart = blockStart,
                blockEnd = blockEnd
            )
        )
        taskDao.insertTask(
            TaskEntity(
                title = "Morning Workout",
                notes = "Gentle yoga + stretch + light cardio",
                estimateMin = 30,
                priority = 1,
                paper = "LINED_PINK",
                tags = "Wellness",
                doneAt = System.currentTimeMillis() - 7200000L
            )
        )
        taskDao.insertTask(
            TaskEntity(
                title = "Design app UI",
                notes = "Refine cassette player animation and washi tape shapes",
                estimateMin = 45,
                priority = 3,
                paper = "LINED_BLUE",
                tags = "Design, Dev",
                doneAt = System.currentTimeMillis() - 3600000L
            )
        )
        taskDao.insertTask(
            TaskEntity(
                title = "Cafe research",
                notes = "Find aesthetic coffee shops in Itahari with good natural lighting",
                estimateMin = 60,
                priority = 2,
                paper = "KRAFT",
                tags = "Inspo"
            )
        )
        taskDao.insertTask(
            TaskEntity(
                title = "Read & Learn",
                notes = "Read 2 chapters of design philosophy book",
                estimateMin = 30,
                priority = 1,
                paper = "LINED_CREAM",
                tags = "Reading"
            )
        )

        // Habits
        val h1 = UUID.randomUUID().toString()
        val h2 = UUID.randomUUID().toString()
        val h3 = UUID.randomUUID().toString()
        val h4 = UUID.randomUUID().toString()

        habitDao.insertHabit(HabitEntity(id = h1, name = "Morning Pages", icon = "book", stampStyle = "CHERRY"))
        habitDao.insertHabit(HabitEntity(id = h2, name = "2L Water Hydration", icon = "water", stampStyle = "STAR"))
        habitDao.insertHabit(HabitEntity(id = h3, name = "Deep Work Session", icon = "tape", stampStyle = "FLOWER"))
        habitDao.insertHabit(HabitEntity(id = h4, name = "Evening Walk & Sunset", icon = "sun", stampStyle = "HEART"))

        // Add some completed habit stamps for today and yesterday
        habitDao.insertLog(HabitLogEntity(habitId = h1, date = todayStr))
        habitDao.insertLog(HabitLogEntity(habitId = h2, date = todayStr))

        val yesterdayCal = Calendar.getInstance()
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterdayCal.time)
        habitDao.insertLog(HabitLogEntity(habitId = h1, date = yesterdayStr))
        habitDao.insertLog(HabitLogEntity(habitId = h2, date = yesterdayStr))
        habitDao.insertLog(HabitLogEntity(habitId = h3, date = yesterdayStr))

        // Initial Focus Session
        focusDao.insertSession(
            FocusSessionEntity(
                taskTitle = "Morning Routine & Focus",
                plannedSec = 1500,
                actualSec = 1500,
                completed = true,
                tape = "25/5",
                reflectionStars = 3
            )
        )

        // Initial Stamps
        stampDao.insertStamp(StampEntity(kind = "SEAL", label = "Welcome Wax Seal", earnedAt = System.currentTimeMillis()))
        stampDao.insertStamp(StampEntity(kind = "SESSION", label = "First Tape Cassette", earnedAt = System.currentTimeMillis()))
        stampDao.insertStamp(StampEntity(kind = "HABIT", label = "Morning Habit Stamp", earnedAt = System.currentTimeMillis()))

        // Initial Board
        val boardId = "dream_cafe_board"
        boardDao.insertBoard(BoardEntity(id = boardId, title = "A Good Day • Dream Café", bg = "GINGHAM"))

        boardDao.insertBoardItem(
            BoardItemEntity(
                boardId = boardId,
                type = "STICKER",
                payload = "cat",
                x = 60f,
                y = 120f,
                scale = 1.1f,
                rotation = -4f,
                z = 1
            )
        )
        boardDao.insertBoardItem(
            BoardItemEntity(
                boardId = boardId,
                type = "STICKER",
                payload = "cherry",
                x = 220f,
                y = 90f,
                scale = 1.0f,
                rotation = 6f,
                z = 2
            )
        )
        boardDao.insertBoardItem(
            BoardItemEntity(
                boardId = boardId,
                type = "TEXT",
                payload = "note to self:\n• be proud\n• keep going\n• good things take time ♥",
                x = 80f,
                y = 340f,
                scale = 1.0f,
                rotation = 3f,
                z = 3
            )
        )
        boardDao.insertBoardItem(
            BoardItemEntity(
                boardId = boardId,
                type = "STICKER",
                payload = "seal",
                x = 240f,
                y = 320f,
                scale = 1.2f,
                rotation = 0f,
                z = 4
            )
        )
        boardDao.insertBoardItem(
            BoardItemEntity(
                boardId = boardId,
                type = "TAPE",
                payload = "rose",
                x = 100f,
                y = 70f,
                scale = 1.0f,
                rotation = -8f,
                z = 5
            )
        )

        // Ideas
        ideaDao.insertIdea(IdeaEntity(text = "Try botanical illustration in Gouache", color = "YELLOW"))
        ideaDao.insertIdea(IdeaEntity(text = "Weekly recap reel with paper tear sound", color = "PINK"))
        ideaDao.insertIdea(IdeaEntity(text = "Bake honey cinnamon loaf for cozy afternoon", color = "CREAM"))

        // Journal
        journalDao.insertEntry(
            JournalEntity(
                date = todayStr,
                mood = "Inspired",
                prompt = "What brought you gentle joy today?",
                body = "Sipped warm roasted coffee while watching golden morning light pour over my paper planner. Finished cutting the YouTube intro sequence and felt so grateful for small peaceful rituals.",
                stickerKeys = "cherry,cat,coffee"
            )
        )
    }
}
