package com.example.vocabmaster.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vocabmaster.R
import com.example.vocabmaster.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val tag = "NotificationWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Work started")
            val database = AppDatabase.getDatabase(applicationContext)
            val count = database.wordDao().getUnmemorizedWords().first().size
            Log.d(tag, "Unmemorized words count: $count")

            if (count > 0) {
                showNotification(
                    title = "Пора повторить слова!",
                    message = "У вас есть $count слов для повторения"
                )
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(tag, "Error in notification worker", e)
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        try {
            val channelId = "vocab_reminder_channel"
            createNotificationChannel(channelId)

            val iconRes = R.drawable.ic_launcher_foreground

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            val notificationManager = NotificationManagerCompat.from(applicationContext)

            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.e(tag, "Missing POST_NOTIFICATIONS permission")
                return
            }

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            Log.d(tag, "Notification shown with title: '$title'")
        } catch (e: Exception) {
            Log.e(tag, "Error showing notification", e)
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Напоминания о словах"
            val descriptionText = "Напоминания о необходимости повторения слов"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d(tag, "Notification channel created: $channelId")
        }
    }
}
