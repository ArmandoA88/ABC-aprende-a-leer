package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abcaprende.leer.R
import com.abcaprende.leer.services.TTSService
import com.abcaprende.leer.services.VoiceRecognitionService

/**
 * Módulo 2 mejorado:
 *  - Imagen clave
 *  - Animación de trazo (placeholder)
 *  - Audio fonético (TTS temporal)
 *  - Reconocimiento de voz (placeholder)
 *  - Mecánica de puntaje y retroalimentación
 */
@Composable
fun Nivel2Screen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val voiceService = remember { VoiceRecognitionService(context) }
    val letras = listOf(
        Triple("A", R.drawable.ic_a, "a"),
        Triple("M", R.drawable.ic_mama, "m"),
        Triple("S", R.drawable.ic_sol, "s"),
        Triple("C", R.drawable.ic_casa, "c")
    )
    val letra = remember { mutableStateOf(letras.random()) }
    var puntaje by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Letra: ${letra.value.first}",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = letra.value.second),
                contentDescription = "Imagen para letra ${letra.value.first}",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                ttsService.speak("Esta es la letra ${letra.value.first}")
            }) {
                Text("Escuchar sonido de la letra")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                ttsService.speak("Animación de trazo no implementada aún")
            }) {
                Text("Ver animación de trazo")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                ttsService.speak("Función de reconocimiento de voz en desarrollo")
            }) {
                Text("Pronuncia la letra")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Puntaje: $puntaje")

            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                letras.shuffled().forEach {
                    Button(onClick = {
                        if (it.first == letra.value.first) {
                            puntaje += 5
                            ttsService.speak("¡Muy bien!")
                        } else {
                            ttsService.speak("Intenta de nuevo")
                        }
                    }) {
                        Text(it.first)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = { navController?.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}
