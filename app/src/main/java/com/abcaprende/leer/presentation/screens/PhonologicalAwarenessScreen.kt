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
 * Módulo 1 mejorado:
 * - Más sonidos e imágenes aleatorias.
 * - Retroalimentación TTS.
 * - Placeholder de animación de celebración.
 * - Reconocimiento de voz básico.
 */
@Composable
fun PhonologicalAwarenessScreen(
    navController: NavController? = null,
    letraObjetivo: String? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val voiceService = remember { VoiceRecognitionService(context) }
    val items = listOf(
        Triple("ma", R.drawable.ic_mama, "ma"),
        Triple("me", R.drawable.ic_mesa, "me"),
        Triple("so", R.drawable.ic_sol, "so"),
        Triple("ca", R.drawable.ic_casa, "ca")
    )
    val currentItem by remember { mutableStateOf(items.random()) }
    var puntaje by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Escucha y selecciona la sílaba correcta", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = currentItem.second),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { triple ->
                    Button(onClick = {
                        if (triple.first == currentItem.first) {
                            ttsService.speak("¡Correcto!")
                            puntaje += 10
                            // Placeholder de animación: cambiar color temporal
                        } else {
                            ttsService.speak("No, intenta de nuevo")
                        }
                    }) {
                        Text(triple.first)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Puntaje: $puntaje")

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                ttsService.speak("Pronuncia la sílaba ${currentItem.third}")
                // Placeholder: no conecta aún con reconocimiento de voz real
            }) {
                Text("Pronunciar")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController?.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}
