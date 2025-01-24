package com.dinesh.screenrecorderapp.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinesh.screenrecorderapp.config.ScreenRecordConfig
import com.dinesh.screenrecorderapp.service.ScreenRecordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@RequiresApi(Build.VERSION_CODES.O)
class ScreenRecordingViewModel : ViewModel() {


    val isRecording = ScreenRecordService.isServiceRunning
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    @RequiresApi(Build.VERSION_CODES.O)
    fun startRecording(context: Context, config: ScreenRecordConfig) {
        val serviceIntent = Intent(context, ScreenRecordService::class.java).apply {
            action = ScreenRecordService.START_RECORDING
            putExtra(ScreenRecordService.KEY_RECORDING_CONFIG, config)
        }
        context.startForegroundService(serviceIntent)
    }

    fun stopRecording(context: Context) {
        Intent(context, ScreenRecordService::class.java).also {
            it.action = ScreenRecordService.STOP_RECORDING
            context.startForegroundService(it)
        }
    }
}