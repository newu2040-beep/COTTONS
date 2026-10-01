package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val notes: String = "",
    val dueAt: Long? = null,
    val estimateMin: Int? = null,
    val priority: Int = 1, // 1 to 3 stars
    val paper: String = "LINED_CREAM",
    val tags: String = "", // comma-separated
    val repeatRule: String? = null,
    val parentId: String? = null,
    val doneAt: Long? = null,
    val blockStart: Long? = null,
    val blockEnd: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt
) {
    val isDone: Boolean get() = doneAt != null
    val tagList: List<String>
        get() = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val taskTitle: String? = null,
    val plannedSec: Int,
    val actualSec: Int,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val completed: Boolean = true,
    val tape: String = "25/5",
    val reflectionStars: Int = 3
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String = "cherry",
    val cadence: String = "DAILY",
    val targetPerPeriod: Int = 1,
    val stampStyle: String = "FLOWER"
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: String,
    val date: String, // yyyy-MM-dd
    val count: Int = 1
)

@Entity(tableName = "stamps")
data class StampEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val kind: String, // "SESSION", "SEAL", "HABIT", "BONUS"
    val earnedAt: Long = System.currentTimeMillis(),
    val refId: String? = null,
    val label: String = "Stamp"
)

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val bg: String = "GINGHAM",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "board_items")
data class BoardItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val boardId: String,
    val type: String, // "STICKER", "PHOTO", "TEXT", "TAPE", "SCRAP"
    val payload: String,
    val x: Float = 100f,
    val y: Float = 100f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val z: Int = 0
)

@Entity(tableName = "journal")
data class JournalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String, // yyyy-MM-dd
    val mood: String = "Cozy",
    val prompt: String = "What brought you gentle joy today?",
    val body: String = "",
    val stickerKeys: String = "cherry,cat"
)

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val text: String,
    val color: String = "YELLOW",
    val createdAt: Long = System.currentTimeMillis()
)

enum class FocusPhase { IDLE, FOCUS, BREAK, PAUSED, DONE }
enum class AmbientSound(val label: String, val icon: String) {
    NONE("Off", "🔕"),
    RAIN("Rain", "🌧️"),
    CAFE("Café", "☕"),
    LOFI("Lo-fi", "🎧"),
    FOREST("Forest", "🌲"),
    WHITE("White", "🌊")
}
