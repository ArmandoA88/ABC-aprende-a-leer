package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abcaprende.leer.R
import com.abcaprende.leer.services.TTSService
import com.abcaprende.leer.services.VoiceRecognitionService

/**
 * Módulo 6 mejorado:
 * - Refuerzo fonológico filtrado por letra.
 * - Sistema de recompensas visuales.
 * - Registro de puntaje.
 * - Reconocimiento de voz.
 */
@Composable
fun Nivel6Screen(
    navController: NavController? = null,
    letra: String? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val voiceService = remember { VoiceRecognitionService(context) }
    var puntaje by remember { mutableStateOf(0) }
    val palabras = listOf("casa", "mesa", "mama", "sol")
    val imagenes = mapOf(
        "casa" to R.drawable.ic_casa,
        "mesa" to R.drawable.ic_mesa,
        "mama" to R.drawable.ic_mama,
        "sol" to R.drawable.ic_sol
    )
    val palabra = remember { mutableStateOf(palabras.random()) }

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
            Text("Refuerzo de la letra: ${letra ?: "?"}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = imagenes[palabra.value] ?: R.drawable.ic_casa),
                contentDescription = palabra.value,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                palabras.shuffled().forEach {
                    Button(onClick = {
                        if (it == palabra.value) {
                            puntaje += 5
                            ttsService.speak("¡Muy bien!")
                        } else {
                            ttsService.speak("No, inténtalo otra vez")
                        }
                    }) {
                        Text(it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Puntaje: $puntaje")

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                ttsService.speak("Pronuncia la palabra ${palabra.value}")
                // Placeholder para reconocimiento de voz real
                // voiceService.startListening(...)
            }) {
                Text("Pronunciar palabra")
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController?.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}
