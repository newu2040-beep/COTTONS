package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.BoardEntity
import com.example.model.BoardItemEntity
import com.example.model.FocusSessionEntity
import com.example.model.HabitEntity
import com.example.model.HabitLogEntity
import com.example.model.IdeaEntity
import com.example.model.JournalEntity
import com.example.model.StampEntity
import com.example.model.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        FocusSessionEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        StampEntity::class,
        BoardEntity::class,
        BoardItemEntity::class,
        JournalEntity::class,
        IdeaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CottonsDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun focusDao(): FocusDao
    abstract fun habitDao(): HabitDao
    abstract fun stampDao(): StampDao
    abstract fun boardDao(): BoardDao
    abstract fun journalDao(): JournalDao
    abstract fun ideaDao(): IdeaDao

    companion object {
        @Volatile
        private var INSTANCE: CottonsDatabase? = null

        fun getInstance(context: Context): CottonsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CottonsDatabase::class.java,
                    "cottons.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
