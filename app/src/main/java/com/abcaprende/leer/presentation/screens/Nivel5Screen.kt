package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abcaprende.leer.services.TTSService

/**
 * Módulo 5 mejorado:
 *  - Texto con resaltado progresivo
 *  - Toque para escuchar palabra
 *  - Puntaje por clics correctos
 *  - Reinicio
 */
@Composable
fun Nivel5Screen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ttsService = remember { TTSService(context) }
    val texto = listOf("La", "mama", "mima", "a", "la", "lama")
    var palabraActual by remember { mutableStateOf(0) }
    var puntaje by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Lectura guiada:",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sustituimos FlowRow por Row con wrap para compatibilidad
            Column {
                var rowItems = mutableListOf<String>()
                texto.forEachIndexed { index, palabra ->
                    if (rowItems.size < 3) {
                        rowItems.add(palabra)
                    }
                    if (rowItems.size == 3 || index == texto.lastIndex) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEachIndexed { idx, p ->
                                val actualIndex = index - rowItems.size + 1 + idx
                                Text(
                                    text = p,
                                    modifier = Modifier
                                        .background(
                                            if (actualIndex == palabraActual) Color.Yellow else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            palabraActual = actualIndex
                                            ttsService.speak(p)
                                            puntaje += 5
                                        }
                                        .padding(4.dp),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        rowItems = mutableListOf()
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Puntaje: $puntaje")

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                palabraActual = 0
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
