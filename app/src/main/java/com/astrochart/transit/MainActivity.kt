package com.astrochart.transit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.astrochart.transit.ui.TransitScreen
import com.astrochart.transit.ui.theme.AstroTransitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstroTransitTheme {
                TransitScreen()
            }
        }
    }
}
