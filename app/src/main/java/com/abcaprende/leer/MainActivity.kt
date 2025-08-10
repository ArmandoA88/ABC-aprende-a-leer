package com.abcaprende.leer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.presentation.screens.HomeScreen
import com.abcaprende.leer.presentation.screens.VowelSelectionScreen
import com.abcaprende.leer.presentation.screens.VowelLearningScreen
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
        
        // Pantalla de selección de vocales
        composable("letter_selection") {
            VowelSelectionScreen(navController = navController)
        }
        
        // Pantalla de aprendizaje de vocal específica
        composable("vowel_activity/{vowel}") { backStackEntry ->
            val vowel = backStackEntry.arguments?.getString("vowel") ?: ""
            VowelLearningScreen(
                navController = navController,
                vowel = vowel
            )
        }
        
        composable("letter_activity/{letter}") {
            ComingSoonScreen(navController = navController, title = "Actividad de Letra")
        }
        
        composable("trace/{letter}") {
            ComingSoonScreen(navController = navController, title = "Trazar Letra")
        }
        
        composable("voice/{letter}") {
            ComingSoonScreen(navController = navController, title = "Reconocimiento de Voz")
        }
        
        composable("minigame/{letter}") {
            ComingSoonScreen(navController = navController, title = "Mini Juego")
        }
        
        composable("parent") {
            ComingSoonScreen(navController = navController, title = "Panel de Padres")
        }
    }
}

@Composable
fun ComingSoonScreen(
    navController: NavController,
    title: String
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚧",
                style = MaterialTheme.typography.displayLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Próximamente",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text("Volver")
            }
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
