package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.FocusSessionEntity
import com.example.model.HabitEntity
import com.example.model.HabitLogEntity
import com.example.model.TaskEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExportManager {

    fun exportToPdf(
        context: Context,
        tasks: List<TaskEntity>,
        sessions: List<FocusSessionEntity>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Uri {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

        // Header Background
        paint.color = Color.parseColor("#FFFDF8")
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Accent top bar
        paint.color = Color.parseColor("#C8283C")
        canvas.drawRect(0f, 0f, 595f, 16f, paint)

        var y = 50f

        // Document Title
        paint.color = Color.parseColor("#2A1215")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("COTTONS · STATIONERY REPORT", 40f, y, paint)

        y += 20f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = Color.parseColor("#805A60")
        canvas.drawText("Exported on $dateStr • Plan • Focus • Create", 40f, y, paint)

        y += 16f
        // Divider
        paint.strokeWidth = 1.5f
        paint.color = Color.parseColor("#DBCBC4")
        canvas.drawLine(40f, y, 555f, y, paint)

        // Stats strip
        y += 24f
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.color = Color.parseColor("#C8283C")
        val doneCount = tasks.count { it.isDone }
        val totalFocusMin = sessions.sumOf { it.actualSec } / 60
        canvas.drawText("TASKS: $doneCount/${tasks.size} DONE   |   FOCUS: ${totalFocusMin}m   |   HABITS: ${habits.size}", 40f, y, paint)

        // Section: Tasks
        y += 28f
        paint.color = Color.parseColor("#2A1215")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("TASKS & NOTES", 40f, y, paint)

        y += 18f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        tasks.take(16).forEach { task ->
            val status = if (task.isDone) "[X]" else "[ ]"
            val stars = "★".repeat(task.priority)
            val est = if (task.estimateMin != null) "(${task.estimateMin}m)" else ""
            val line = "$status ${task.title} $est  $stars"

            paint.color = if (task.isDone) Color.parseColor("#9E8E8A") else Color.parseColor("#2A1215")
            canvas.drawText(line, 45f, y, paint)

            if (task.notes.isNotBlank()) {
                y += 12f
                paint.color = Color.parseColor("#857672")
                val shortNote = if (task.notes.length > 70) task.notes.substring(0, 67) + "..." else task.notes
                canvas.drawText("      $shortNote", 45f, y, paint)
            }
            y += 16f
        }

        // Section: Focus Sessions
        y += 12f
        paint.color = Color.parseColor("#2A1215")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("FOCUS CASSETTE SESSIONS", 40f, y, paint)

        y += 18f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        sessions.take(6).forEach { session ->
            val mins = session.actualSec / 60
            val sessionDate = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(session.startedAt))
            val stars = "★".repeat(session.reflectionStars)
            val line = "• ${session.taskTitle ?: "Deep Work"} — ${mins} mins ($stars)  [$sessionDate]"
            paint.color = Color.parseColor("#2A1215")
            canvas.drawText(line, 45f, y, paint)
            y += 15f
        }

        // Section: Habits
        y += 12f
        paint.color = Color.parseColor("#2A1215")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("HABIT STAMP BOOK", 40f, y, paint)

        y += 18f
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        habits.forEach { habit ->
            val stampCount = logs.count { it.habitId == habit.id }
            val line = "• ${habit.name}  —  $stampCount stamps collected"
            paint.color = Color.parseColor("#2A1215")
            canvas.drawText(line, 45f, y, paint)
            y += 15f
        }

        // Footer
        paint.color = Color.parseColor("#A89C94")
        paint.textSize = 9f
        canvas.drawLine(40f, 810f, 555f, 810f, paint)
        canvas.drawText("Cottons Planner · Rahul Shah / Editingcells · Offline-first Android", 40f, 824f, paint)

        document.finishPage(page)

        val file = File(context.cacheDir, "cottons_export_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun exportToCsv(
        context: Context,
        tasks: List<TaskEntity>,
        sessions: List<FocusSessionEntity>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Uri {
        val file = File(context.cacheDir, "cottons_export_${System.currentTimeMillis()}.csv")
        file.bufferedWriter().use { writer ->
            writer.write("\uFEFF") // UTF-8 BOM
            writer.write("--- TASKS ---\n")
            writer.write("ID,Title,Notes,EstimateMin,Priority,IsDone,Tags,CreatedAt\n")
            tasks.forEach { t ->
                val title = t.title.replace("\"", "\"\"")
                val notes = t.notes.replace("\"", "\"\"")
                val tags = t.tags.replace("\"", "\"\"")
                writer.write("\"${t.id}\",\"$title\",\"$notes\",${t.estimateMin ?: 0},${t.priority},${t.isDone},\"$tags\",${t.createdAt}\n")
            }

            writer.write("\n--- FOCUS SESSIONS ---\n")
            writer.write("ID,TaskTitle,PlannedMinutes,ActualMinutes,Completed,ReflectionStars,StartedAt\n")
            sessions.forEach { s ->
                val title = (s.taskTitle ?: "Focus").replace("\"", "\"\"")
                writer.write("\"${s.id}\",\"$title\",${s.plannedSec / 60},${s.actualSec / 60},${s.completed},${s.reflectionStars},${s.startedAt}\n")
            }

            writer.write("\n--- HABITS ---\n")
            writer.write("ID,Name,Icon,TotalStampsEarned\n")
            habits.forEach { h ->
                val stampCount = logs.count { it.habitId == h.id }
                val name = h.name.replace("\"", "\"\"")
                writer.write("\"${h.id}\",\"$name\",\"${h.icon}\",$stampCount\n")
            }
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun exportToTxt(
        context: Context,
        tasks: List<TaskEntity>,
        sessions: List<FocusSessionEntity>,
        habits: List<HabitEntity>,
        logs: List<HabitLogEntity>
    ): Uri {
        val file = File(context.cacheDir, "cottons_export_${System.currentTimeMillis()}.txt")
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        file.bufferedWriter().use { writer ->
            writer.write("=====================================================\n")
            writer.write("             COTTONS STATIONERY SUMMARY              \n")
            writer.write("            Plan • Focus • Create • Cozy             \n")
            writer.write("=====================================================\n")
            writer.write("Export Date: $dateStr\n")
            writer.write("Total Tasks: ${tasks.size} (Completed: ${tasks.count { it.isDone }})\n")
            writer.write("Total Focus Time: ${sessions.sumOf { it.actualSec } / 60} minutes\n\n")

            writer.write("-------------------- [ TASKS ] ----------------------\n")
            tasks.forEach { t ->
                val status = if (t.isDone) "[X]" else "[ ]"
                val stars = "★".repeat(t.priority)
                val est = if (t.estimateMin != null) "(${t.estimateMin}m)" else ""
                writer.write("$status ${t.title} $est  $stars\n")
                if (t.notes.isNotBlank()) {
                    writer.write("    Note: ${t.notes}\n")
                }
                if (t.tags.isNotBlank()) {
                    writer.write("    Tags: #${t.tags}\n")
                }
            }

            writer.write("\n---------------- [ FOCUS TAPES ] --------------------\n")
            sessions.forEach { s ->
                val mins = s.actualSec / 60
                val date = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault()).format(Date(s.startedAt))
                writer.write("• ${s.taskTitle ?: "Deep Work"} — ${mins}m | Rating: ${s.reflectionStars}/3 ★ | $date\n")
            }

            writer.write("\n----------------- [ HABIT STAMPS ] ------------------\n")
            habits.forEach { h ->
                val stampCount = logs.count { it.habitId == h.id }
                writer.write("• ${h.name} — $stampCount stamps collected\n")
            }

            writer.write("\n=====================================================\n")
            writer.write("Crafted with Cottons · Rahul Shah / Editingcells\n")
            writer.write("=====================================================\n")
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun shareFile(context: Context, uri: Uri, mimeType: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
