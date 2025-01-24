package com.dinesh.screenrecorderapp.model

import android.net.Uri

data class Recording(
    val uri: Uri,
    val displayName: String,
    val size: Long,
    val dateAdded: Long
)
