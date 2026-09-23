package com.moneytracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moneytracker.app.ui.navigation.MoneyTrackerNav
import com.moneytracker.app.ui.theme.MoneyTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as MoneyTrackerApplication).container
        setContent {
            MoneyTrackerTheme {
                MoneyTrackerNav(container = container)
            }
        }
    }
}
