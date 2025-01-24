package com.dinesh.screenrecorderapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dinesh.screenrecorderapp.model.Recording
import com.dinesh.screenrecorderapp.viewmodel.RecordingViewModel

@Composable
fun RecordingListScreen(recordingsViewModel: RecordingViewModel) {
    val recordings by recordingsViewModel.recording.collectAsStateWithLifecycle()
    var showRenameDialog by remember {
        mutableStateOf(false)
    }
    var recordingToRename by remember {
        mutableStateOf<Recording?>(null)
    }

    val newName by remember {
        mutableStateOf("")
    }

    var showDelateDialog by remember {
        mutableStateOf(false)
    }

    var recordingToDelete by remember {
        mutableStateOf<Recording?>(null)
    }



}