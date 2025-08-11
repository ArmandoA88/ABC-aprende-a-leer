package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abcaprende.leer.services.TTSService

/**
 * Módulo 3 mejorado:
 *  - Tarjetas de sílabas arrastrables
 *  - Área para formar palabras
 *  - Feedback con TTS
 *  - Sistema de puntaje
 */
@Composable
fun Nivel3Screen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val silabas = remember { mutableStateListOf("ma", "me", "mi", "mo", "mu") }
    val zonaPalabra = remember { mutableStateListOf<String>() }
    var puntaje by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Formar palabra:",
                style = MaterialTheme.typography.headlineMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = zonaPalabra.joinToString(""),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Text("Puntaje: $puntaje")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                silabas.forEach { silaba ->
                    Button(
                        onClick = {
                            zonaPalabra.add(silaba)
                            ttsService.speak(silaba)
                            puntaje += 5
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                    ) {
                        Text(silaba, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            if (zonaPalabra.size >= 2) {
                Button(onClick = {
                    ttsService.speak("Palabra formada: ${zonaPalabra.joinToString("")}")
                }) {
                    Text("Escuchar palabra")
                }
            }

            Button(onClick = {
                zonaPalabra.clear()
                puntaje = 0
            }) {
                Text("Reiniciar")
            }
        }
    }
}
