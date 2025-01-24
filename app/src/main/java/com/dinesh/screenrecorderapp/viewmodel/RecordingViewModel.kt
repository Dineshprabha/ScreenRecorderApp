package com.dinesh.screenrecorderapp.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dinesh.screenrecorderapp.model.Recording
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordingViewModel(
    application: Application
) : AndroidViewModel(application){


    private val _recordings = MutableStateFlow<List<Recording>>(emptyList())
    val recording = _recordings.asStateFlow()

    fun loadRecording() {
        viewModelScope.launch {
            val loaded = mutableListOf<Recording>()

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.RELATIVE_PATH
            )

            val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"

            val selectionArgs = arrayOf("%Movies/Recordings2%")
            val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

                while (cursor.moveToNext()) {
                    val id =  cursor.getLong(idCol)
                    val displayName = cursor.getString(nameCol)
                    val size = cursor.getLong(sizeCol)
                    val dateAdded = cursor.getLong(dateCol)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    loaded.add(
                        Recording(
                            uri = contentUri,
                            displayName = displayName,
                            size = size,
                            dateAdded = dateAdded
                        )
                    )
                }
            }
            _recordings.value = loaded
        }
    }

    fun deleteRecording(recording: Recording) {
        viewModelScope.launch {
            getApplication<Application>().contentResolver.delete(
                recording.uri,
                null,
                null,
            )
            loadRecording()
        }
    }

    fun renameRecording(recording: Recording, newName: String) {
        viewModelScope.launch {
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, newName)
            }

            getApplication<Application>().contentResolver.update(
                recording.uri,
                contentValues,
                null,
                null
            )

            loadRecording()
        }
    }

    fun playRecording(recording: Recording) {
        val context = getApplication<Application>().applicationContext
        val playIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(recording.uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(playIntent)
    }

    fun shareRecording(recording: Recording) {
        val context = getApplication<Application>().applicationContext
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, recording.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Recording Via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}