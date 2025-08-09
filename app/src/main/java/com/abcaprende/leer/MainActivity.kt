package com.abcaprende.leer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.presentation.screens.HomeScreen
import com.abcaprende.leer.presentation.screens.LetterSelectionScreen
import com.abcaprende.leer.presentation.screens.LetterActivityScreen
import com.abcaprende.leer.presentation.screens.TraceScreen
import com.abcaprende.leer.presentation.screens.VoiceScreen
import com.abcaprende.leer.presentation.screens.MiniGameScreen
import com.abcaprende.leer.presentation.screens.ParentScreen
import com.abcaprende.leer.presentation.viewmodels.MainViewModel
import com.abcaprende.leer.ui.theme.ABCAprendeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            ABCAprendeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ABCAprendeApp()
                }
            }
        }
    }
}

@Composable
fun ABCAprendeApp() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
        
        composable("letter_selection") {
            LetterSelectionScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
        
        composable("letter_activity/{letter}") { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            LetterActivityScreen(
                navController = navController,
                viewModel = mainViewModel,
                letter = letter
            )
        }
        
        composable("trace/{letter}") { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            TraceScreen(
                navController = navController,
                viewModel = mainViewModel,
                letter = letter
            )
        }
        
        composable("voice/{letter}") { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            VoiceScreen(
                navController = navController,
                viewModel = mainViewModel,
                letter = letter
            )
        }
        
        composable("minigame/{letter}") { backStackEntry ->
            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
            MiniGameScreen(
                navController = navController,
                viewModel = mainViewModel,
                letter = letter
            )
        }
        
        composable("parent") {
            ParentScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ABCAprendeAppPreview() {
    ABCAprendeTheme {
        ABCAprendeApp()
    }
}
