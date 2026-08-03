package com.example.smsintelligence

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smsintelligence.data.db.AppDatabase
import com.example.smsintelligence.data.repository.SmsRepository
import com.example.smsintelligence.sms.SmsSyncWorker
import com.example.smsintelligence.ui.screens.DashboardScreen

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            triggerSmsSync()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = SmsRepository(database.smsDao())

        setContent {
            DashboardScreen(
                repository = repository,
                onSyncClicked = { checkPermissionAndSync() }
            )
        }
    }

    private fun checkPermissionAndSync() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            triggerSmsSync()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun triggerSmsSync() {
        val syncWork = OneTimeWorkRequestBuilder<SmsSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(syncWork)
    }
}
