package com.tifd.projectcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tifd.projectcomposed.navigation.Screen
import com.tifd.projectcomposed.screen.MatkulScreen
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.screen.TugasScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                MainScreen()
            }
        }
    }
}


