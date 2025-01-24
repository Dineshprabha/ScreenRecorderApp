package com.dinesh.screenrecorderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dinesh.screenrecorderapp.ui.ScreenRecorderApp
import com.dinesh.screenrecorderapp.ui.theme.ScreenRecorderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenRecorderApp()
        }
    }
}
