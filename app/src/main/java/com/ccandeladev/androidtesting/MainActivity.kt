package com.ccandeladev.androidtesting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ccandeladev.androidtesting.core.presentation.navigation.NavGraph
import com.ccandeladev.androidtesting.ui.theme.AndroidTestingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTestingTheme {
                Scaffold() { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        NavGraph()
                    }

                }

            }
        }
    }
}



