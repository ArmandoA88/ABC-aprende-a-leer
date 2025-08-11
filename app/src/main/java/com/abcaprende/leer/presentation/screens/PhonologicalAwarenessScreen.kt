package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abcaprende.leer.R
import com.abcaprende.leer.services.TTSService

@Composable
fun PhonologicalAwarenessScreen(onNext: () -> Unit, letraObjetivo: String? = null) {
    val context = LocalContext.current
    val ttsService = remember { TTSService(context) }
    val targetSound = remember { mutableStateOf("m") }
    var feedback by remember { mutableStateOf("") }
    var showConfetti by remember { mutableStateOf(false) }

    val predefinedRounds = listOf(
        RoundData("m", listOf(
            ChoiceData(R.drawable.ic_mama, true),
            ChoiceData(R.drawable.ic_sol, false)
        )),
        RoundData("s", listOf(
            ChoiceData(R.drawable.ic_sol, true),
            ChoiceData(R.drawable.ic_mesa, false)
        )),
        RoundData("c", listOf(
            ChoiceData(R.drawable.ic_casa, true),
            ChoiceData(R.drawable.ic_mama, false)
        ))
    )

    val rounds = if (!letraObjetivo.isNullOrBlank()) {
        predefinedRounds.filter { it.sound.equals(letraObjetivo, ignoreCase = true) }
            .ifEmpty { predefinedRounds }
    } else {
        predefinedRounds
    }
    var currentRound by remember { mutableStateOf(0) }

    LaunchedEffect(currentRound) {
        targetSound.value = rounds[currentRound].sound
        ttsService.speak("Escucha. ¿Qué imagen empieza con el sonido ${targetSound.value}?")
        showConfetti = false
    }

    // Animación no implementada porque la librería de Lottie aún no está configurada en build.gradle
    // Se puede añadir posteriormente junto con la dependencia:
    // implementation "com.airbnb.android:lottie-compose:x.x.x"

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7E7CE)),
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Encuentra el sonido: ${targetSound.value}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize(0.8f)
                ) {
                    rounds[currentRound].choices.forEach { choice ->
                        ImageChoice(
                            resId = choice.resId,
                            correct = choice.correct,
                            onResult = { correct ->
                                if (correct) {
                                    ttsService.speak("¡Muy bien!")
                                    feedback = "✔ Correcto"
                                    showConfetti = true
                                    if (currentRound < rounds.size - 1) {
                                        currentRound++
                                    } else {
                                        onNext()
                                    }
                                } else {
                                    ttsService.speak("Intenta otra vez")
                                    feedback = "❌ Incorrecto"
                                    showConfetti = false
                                }
                            }
                        )
                    }
                }
                Text(text = feedback, fontSize = 22.sp, fontWeight = FontWeight.Medium)
            }
            // Placeholder para futura animación
        }
    }
}

data class RoundData(val sound: String, val choices: List<ChoiceData>)
data class ChoiceData(val resId: Int, val correct: Boolean)

@Composable
fun ImageChoice(resId: Int, correct: Boolean, onResult: (Boolean) -> Unit) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onResult(correct) }
            .padding(8.dp)
    )
}
