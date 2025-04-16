package com.github.nteditor.zapretkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.nteditor.zapretkotlin.navbar.NavBar
import com.github.nteditor.zapretkotlin.ui.theme.ZapretTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZapretTheme {
                NavBar()
            }
        }
    }
}