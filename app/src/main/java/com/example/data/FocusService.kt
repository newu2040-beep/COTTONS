package com.example.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.CottonsApplication
import com.example.MainActivity
import com.example.model.FocusPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class FocusSessionState(
    val phase: FocusPhase = FocusPhase.IDLE,
    val taskTitle: String = "Deep Work Tape",
    val plannedSec: Int = 25 * 60,
    val remainingSec: Int = 25 * 60,
    val cycle: Int = 1,
    val totalCycles: Int = 4
)

class FocusService : Service() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val planned = intent.getIntExtra(EXTRA_PLANNED_SEC, 25 * 60)
                val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Deep Work Tape"
                startTimer(planned, title)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(plannedSec: Int, title: String) {
        _sessionState.value = FocusSessionState(
            phase = FocusPhase.FOCUS,
            taskTitle = title,
            plannedSec = plannedSec,
            remainingSec = plannedSec
        )
        startForeground(NOTIFICATION_ID, buildNotification())
        runCountdown()
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _sessionState.value = _sessionState.value.copy(phase = FocusPhase.PAUSED)
        updateNotification()
    }

    private fun resumeTimer() {
        _sessionState.value = _sessionState.value.copy(phase = FocusPhase.FOCUS)
        runCountdown()
        updateNotification()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _sessionState.value = FocusSessionState(phase = FocusPhase.IDLE)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun runCountdown() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && _sessionState.value.remainingSec > 0) {
                delay(1000)
                val newRemaining = _sessionState.value.remainingSec - 1
                if (newRemaining <= 0) {
                    onFinished()
                    break
                } else {
                    _sessionState.value = _sessionState.value.copy(remainingSec = newRemaining)
                    if (newRemaining % 5 == 0) {
                        updateNotification()
                    }
                }
            }
        }
    }

    private fun onFinished() {
        val app = application as? CottonsApplication
        app?.container?.sounds?.playStampThunk()
        _sessionState.value = _sessionState.value.copy(
            phase = FocusPhase.DONE,
            remainingSec = 0
        )
        updateNotification()
    }

    private fun updateNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val state = _sessionState.value
        val minutes = state.remainingSec / 60
        val seconds = state.remainingSec % 60
        val timeStr = String.format("%02d:%02d", minutes, seconds)

        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseResumeIntent = if (state.phase == FocusPhase.FOCUS) {
            PendingIntent.getService(
                this,
                1,
                Intent(this, FocusService::class.java).apply { action = ACTION_PAUSE },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getService(
                this,
                2,
                Intent(this, FocusService::class.java).apply { action = ACTION_RESUME },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val stopIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, FocusService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val actionTitle = if (state.phase == FocusPhase.FOCUS) "Pause" else "Resume"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("📼 ${state.taskTitle} ($timeStr)")
            .setContentText(if (state.phase == FocusPhase.DONE) "Focus session complete! Earned a stamp." else "Focus Cassette Player is playing")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openIntent)
            .setOngoing(state.phase == FocusPhase.FOCUS)
            .addAction(0, actionTitle, pauseResumeIntent)
            .addAction(0, "Stop", stopIntent)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Cassette Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Focus countdown timer notification with controls"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "cottons_focus_channel"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "action_start"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_RESUME = "action_resume"
        const val ACTION_STOP = "action_stop"

        const val EXTRA_PLANNED_SEC = "extra_planned_sec"
        const val EXTRA_TASK_TITLE = "extra_task_title"

        private val _sessionState = MutableStateFlow(FocusSessionState())
        val sessionState: StateFlow<FocusSessionState> = _sessionState.asStateFlow()

        fun updateStateManually(newState: FocusSessionState) {
            _sessionState.value = newState
        }
    }
}
