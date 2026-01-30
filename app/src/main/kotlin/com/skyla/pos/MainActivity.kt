package com.skyla.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.skyla.pos.navigation.SkylaApp
import com.skyla.pos.ui.theme.SkylaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkylaTheme {
                SkylaApp()
            }
        }
    }
}
