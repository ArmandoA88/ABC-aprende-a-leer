package com.abcaprende.leer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.presentation.screens.HomeScreen
import com.abcaprende.leer.presentation.screens.VowelSelectionScreen
import com.abcaprende.leer.presentation.screens.VowelLearningScreen
import com.abcaprende.leer.presentation.screens.TracingScreen
import com.abcaprende.leer.presentation.screens.EstrellitaModeScreen // Import EstrellitaModeScreen
import com.abcaprende.leer.presentation.screens.ConsonantLearningScreen // Import ConsonantLearningScreen
import com.abcaprende.leer.presentation.viewmodels.MainViewModel
import com.abcaprende.leer.ui.theme.ABCAprendeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Launcher para solicitar múltiples permisos
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Toast.makeText(this, "¡Permisos concedidos! La app funcionará correctamente.", Toast.LENGTH_LONG).show()
        } else {
            val deniedPermissions = permissions.filterValues { !it }.keys
            val message = when {
                deniedPermissions.contains(Manifest.permission.RECORD_AUDIO) -> 
                    "El micrófono es necesario para el reconocimiento de voz. Puedes habilitarlo en Configuración."
                deniedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> 
                    "Las notificaciones ayudan a recordar las lecciones. Puedes habilitarlas en Configuración."
                else -> "Algunos permisos fueron denegados. La app puede no funcionar completamente."
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Solicitar permisos automáticamente al iniciar la app
        requestNecessaryPermissions()
        
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
    
    private fun requestNecessaryPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        // Verificar permiso de micrófono (CRÍTICO para reconocimiento de voz)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        
        // Verificar permiso de notificaciones (solo para Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Solicitar permisos si es necesario
        if (permissionsToRequest.isNotEmpty()) {
            // Mostrar mensaje explicativo antes de solicitar permisos
            Toast.makeText(
                this, 
                "ABC Aprende necesita algunos permisos para funcionar correctamente. ¡Por favor acepta!", 
                Toast.LENGTH_LONG
            ).show()
            
            // Solicitar permisos después de un pequeño delay para que el usuario lea el mensaje
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
            }, 2000) // 2 segundos de delay
        } else {
            // Todos los permisos ya están concedidos
            Toast.makeText(this, "¡Todos los permisos están listos! 🎉", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Método para verificar si todos los permisos críticos están concedidos
    private fun hasAllCriticalPermissions(): Boolean {
        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No se requiere en versiones anteriores
        }
        
        return audioPermission && notificationPermission
    }
    
    override fun onResume() {
        super.onResume()
        // Verificar permisos cada vez que la app vuelve al primer plano
        if (!hasAllCriticalPermissions()) {
            Toast.makeText(
                this, 
                "Algunos permisos están deshabilitados. Ve a Configuración para habilitarlos.", 
                Toast.LENGTH_LONG
            ).show()
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
                initialVowel = vowel // Renamed parameter for clarity
            )
        }
        
        // Nueva pantalla para el aprendizaje secuencial de vocales
        composable("vowel_learning_sequence") {
            VowelLearningScreen(
                navController = navController,
                initialVowel = null // Indicate that it should start from the beginning of the sequence
            )
        }
        
        // Nueva pantalla para el aprendizaje secuencial de consonantes
        composable("consonant_activity_sequence") { backStackEntry ->
            ConsonantLearningScreen(
                navController = navController,
                initialConsonant = null // Indicate that it should start from the beginning of the sequence
            )
        }
        
        // Pantalla de trazado (Nivel 3, shifted from Level 2)
        composable("tracing") {
            TracingScreen(navController = navController)
        }
        
        // New Estrellita Mode Screen
        composable("estrellita_mode") {
            EstrellitaModeScreen(navController = navController)
        }

        // Nuevo Nivel 6 - Refuerzo fonológico después de cada letra en Estrellita
        composable("nivel6/{letra}") { backStackEntry ->
            val letra = backStackEntry.arguments?.getString("letra")
            com.abcaprende.leer.presentation.screens.Nivel6Screen(
                navController = navController,
                letra = letra
            )
        }

        // Estructura base para nuevos módulos
        composable("nivel2") {
            com.abcaprende.leer.presentation.screens.Nivel2Screen()
        }
        composable("nivel3") {
            com.abcaprende.leer.presentation.screens.Nivel3Screen()
        }
        composable("nivel4") {
            com.abcaprende.leer.presentation.screens.Nivel4Screen()
        }
        composable("nivel5") {
            com.abcaprende.leer.presentation.screens.Nivel5Screen()
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
