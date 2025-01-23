package com.dinesh.screenrecorderapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dinesh.screenrecorderapp.ui.theme.CoralRed
import com.dinesh.screenrecorderapp.ui.theme.MintGreen

@Composable
fun ScreenRecordScreen(
    isRecording: Boolean,
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        ElevatedCard(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = if (isRecording) "Recording in progress..." else "Ready to Record",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = {
                        if (!hasNotificationPermission) {
                            onRequestPermission()
                        } else {
                            if (isRecording) {
                                onStopRecording()
                            } else {
                                onStartRecording()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) CoralRed else MintGreen
                    )
                ) {
                    val icon = if (isRecording) Icons.Filled.Check else Icons.Filled.PlayArrow
                    Icon(imageVector = icon, contentDescription = null, tint = Color.Black)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isRecording) "Stop Recording" else "Start Recording",
                        color = Color.Black
                    )
                }
            }

        }

    }

}