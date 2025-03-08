package com.fiax.hdr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.fiax.hdr.ui.components.scaffold.MainScaffold
import com.fiax.hdr.ui.theme.HDRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HDRTheme {
                val navController = rememberNavController()
                MainScaffold(navController)
            }
        }
    }
}

