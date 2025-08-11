package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abcaprende.leer.services.TTSService

/**
 * Módulo 4 mejorado:
 *  - Palabras como rompecabezas de sílabas
 *  - Orden correcto para formar palabra
 *  - Feedback con TTS
 *  - Sistema de puntaje y reinicio
 */
@Composable
fun Nivel4Screen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val palabraObjetivo = listOf("so", "la", "ma")
    var seleccion by remember { mutableStateOf(listOf<String>()) }
    var puntaje by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Arma la palabra",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Progreso: ${seleccion.joinToString("")}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                palabraObjetivo.shuffled().forEach { silaba ->
                    Button(onClick = {
                        seleccion = seleccion + silaba
                        ttsService.speak(silaba)

                        if (seleccion.size == palabraObjetivo.size) {
                            if (seleccion == palabraObjetivo) {
                                ttsService.speak("¡Correcto! La palabra es ${palabraObjetivo.joinToString("")}")
                                puntaje += 10
                            } else {
                                ttsService.speak("Intenta de nuevo")
                            }
                        }
                    }) {
                        Text(silaba)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text("Puntaje: $puntaje")

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                seleccion = listOf()
                puntaje = 0
            }) {
                Text("Reiniciar")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController?.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}
