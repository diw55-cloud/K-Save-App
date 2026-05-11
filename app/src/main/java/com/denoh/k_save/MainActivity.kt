package com.denoh.k_save

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.denoh.k_save.navigation.AppNavHost
import com.denoh.k_save.ui.theme.KSaveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KSaveTheme {
                AppNavHost()
            }
        }
    }
}
