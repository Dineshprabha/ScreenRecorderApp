package com.dinesh.screenrecorderapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dinesh.screenrecorderapp.config.ScreenRecordConfig
import com.dinesh.screenrecorderapp.ui.screens.RecordingListScreen
import com.dinesh.screenrecorderapp.ui.screens.ScreenRecordScreen
import com.dinesh.screenrecorderapp.viewmodel.RecordingViewModel
import com.dinesh.screenrecorderapp.viewmodel.ScreenRecordingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenRecorderApp() {

    val context = LocalContext.current

    val recordingViewModel = viewModel<RecordingViewModel>()
    val screenRecordViewModel = viewModel<ScreenRecordingViewModel>()

    val isRecording by screenRecordViewModel.isRecording.collectAsStateWithLifecycle()

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val mediaProjectionManager = remember {
        context.getSystemService<MediaProjectionManager>()
    }


    val screenCaptureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data ?: return@rememberLauncherForActivityResult
            val config = ScreenRecordConfig(
                resultCode = result.resultCode,
                data = data
            )
            screenRecordViewModel.startRecording(context, config)
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
            hasNotificationPermission = granted
            if (granted && !isRecording) {
                mediaProjectionManager?.let {
                    screenCaptureLauncher.launch(it.createScreenCaptureIntent())
                }
            }
        }

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "Screen Recorder")
                    },
                )

                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Screen Record") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            // Reload recordings when switching to second tab
                            recordingViewModel.loadRecording()
                        },
                        text = { Text("Recordings") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> ScreenRecordScreen(
                    isRecording = isRecording,
                    hasNotificationPermission = hasNotificationPermission,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    onStartRecording = {
                        mediaProjectionManager?.let {
                            screenCaptureLauncher.launch(it.createScreenCaptureIntent())
                        }
                    },
                    onStopRecording = {
                        screenRecordViewModel.stopRecording(context)
                    }
                )
                1 -> RecordingListScreen(
                    recordingsViewModel = recordingViewModel
                )
            }
        }

    }
}