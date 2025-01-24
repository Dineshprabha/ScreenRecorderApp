package com.dinesh.screenrecorderapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dinesh.screenrecorderapp.model.Recording
import com.dinesh.screenrecorderapp.viewmodel.RecordingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordingListScreen(recordingsViewModel: RecordingViewModel) {
    val recordings by recordingsViewModel.recording.collectAsStateWithLifecycle()
    var showRenameDialog by remember {
        mutableStateOf(false)
    }
    var recordingToRename by remember {
        mutableStateOf<Recording?>(null)
    }

    var newName by remember {
        mutableStateOf("")
    }

    var showDelateDialog by remember {
        mutableStateOf(false)
    }

    var recordingToDelete by remember {
        mutableStateOf<Recording?>(null)
    }


    // ---------- RENAME DIALOG ----------
    if (showRenameDialog && recordingToRename != null) {
        AlertDialog(
            onDismissRequest = {
                showRenameDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    val r = recordingToRename ?: return@Button
                    if (newName.isNotBlank()) {
                        val finalName = "${newName}.mp4"
                        recordingsViewModel.renameRecording(r, finalName)
                    }
                    showRenameDialog = false
                    newName = ""
                }) {
                    Text(text = "OK")
                }
            },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { typed ->
                        if (typed.length <= 12) {
                            newName = typed
                        }

                    },
                    label = { Text(text = "New Name (max 12 char") })
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    newName = ""
                }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    //----------------Delete confirmation dialog---------------

    if (showDelateDialog && recordingToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showRenameDialog = false
            },
            title = { Text(text = "Delete Recording") },
            text = {
                Text(text = "Are you sure you want to delete \"${recordingToDelete?.displayName}\"?")
            },
            confirmButton = {
                Button(onClick = {
                    val recToDel = recordingToDelete ?: return@Button
                    recordingsViewModel.deleteRecording(recToDel)
                    showDelateDialog = false
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDelateDialog = false
                }) {
                    Text(text = "Cancel")
                }
            }

        )
    }

    //-------------Main Content ---------------

    if (recordings.isEmpty()){
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "No recordings found", color = Color.Gray)
        }
    }else {
        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ){
            
        }
    }

}

@SuppressLint("RememberReturnType")
@Composable
fun RecordingCard(
    recording: Recording,
    onPlay: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPlay() },
    ) {
        val dateString = remember (recording.dateAdded){
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(recording.dateAdded * 1000L))
        }

        val baseName = recording.displayName.removeSuffix(".mp4")

        val displayName: String = if (baseName.startsWith("video_")) {
            val lastFive = baseName.removePrefix("video_").takeLast(5)
            "video_${lastFive}.mp4"
        }else {
            recording.displayName
        }

        Row (
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier.weight(1f)
            ){
                Text(text = displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "${recording.size / 1024} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row (
                horizontalArrangement = Arrangement.spacedBy(0.5.dp)
            ) {

                IconButton(onClick = onPlay) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription ="Play")
                }
                IconButton(onClick = onShare) {
                    Icon(imageVector = Icons.Default.Share, contentDescription ="Share")
                }
                IconButton(onClick = onRename) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription ="Rename")
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription ="Delete")
                }
            }

        }
    }

}