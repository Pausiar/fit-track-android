package com.pausiar.fit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.pausiar.fit.ui.navigation.FitNavHost
import com.pausiar.fit.ui.theme.Background
import com.pausiar.fit.ui.theme.PausiarFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            PausiarFitTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Background) {
                    FitNavHost()
                }
            }
        }
    }
}
