package com.example.vocabmaster

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vocabmaster.notifications.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class VocabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("VocabApp", "Application created")
        scheduleDailyNotification()
    }

    private fun scheduleDailyNotification() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15, TimeUnit.MINUTES // Для теста используем 15 минут
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "vocab_daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE, // Используем UPDATE вместо KEEP
            workRequest
        )
        Log.d("VocabApp", "Work scheduled")
    }
}