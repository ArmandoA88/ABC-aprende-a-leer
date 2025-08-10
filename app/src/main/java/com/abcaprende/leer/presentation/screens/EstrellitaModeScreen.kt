package com.abcaprende.leer.presentation.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abcaprende.leer.presentation.viewmodels.VowelLearningViewModel
import com.abcaprende.leer.services.TTSService
import java.util.Locale
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign
import com.abcaprende.leer.presentation.screens.EstrellitaTracingContent // Import the TracingContent
import androidx.compose.foundation.rememberScrollState // Import rememberScrollState
import androidx.compose.foundation.horizontalScroll // Import horizontalScroll

enum class EstrellitaStep {
    INTRODUCTION,
    ASSOCIATION,
    TRACING,
    SYLLABLE_CONSTRUCTION
}

@Composable
fun EstrellitaModeScreen(navController: NavController) {
    val context = LocalContext.current
    val ttsService = remember { TTSService(context) }
    val viewModel: VowelLearningViewModel = hiltViewModel()

    val currentLetter = remember { mutableStateOf("M") } // Start with 'M' for example
    var currentStep by remember { mutableStateOf(EstrellitaStep.INTRODUCTION) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        // Initialize TTS in a coroutine
        val job = coroutineScope.launch {
            ttsService.initialize()
        }
        onDispose {
            job.cancel() // Cancel the initialization job if the composable leaves the screen
            ttsService.shutdown()
        }
    }

    LaunchedEffect(currentLetter.value) {
        // Play the sound of the current letter
        ttsService.speak(currentLetter.value)
        kotlinx.coroutines.delay(1000) // Small delay before playing the phrase
        // Play a phonetic phrase associated with the letter
        ttsService.speak("${currentLetter.value} de mamá") // Example phrase for 'M'
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when (currentStep) {
                EstrellitaStep.INTRODUCTION -> {
                    Text(
                        text = currentLetter.value,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 200.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Placeholder for associated image
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aquí va la imagen de Mamá", color = Color.DarkGray)
                    }
                }
                EstrellitaStep.ASSOCIATION -> {
                    Text("Association Content Here")
                    // TODO: Implement Association Visual and Auditory
                }
                EstrellitaStep.TRACING -> {
                    EstrellitaTracingContent(letter = currentLetter.value, ttsService = ttsService)
                }
                EstrellitaStep.SYLLABLE_CONSTRUCTION -> {
                    SyllableConstructionContent(ttsService = ttsService)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Volver")
                }

                Button(onClick = {
                    currentStep = when (currentStep) {
                        EstrellitaStep.INTRODUCTION -> EstrellitaStep.ASSOCIATION
                        EstrellitaStep.ASSOCIATION -> EstrellitaStep.TRACING
                        EstrellitaStep.TRACING -> EstrellitaStep.SYLLABLE_CONSTRUCTION
                        EstrellitaStep.SYLLABLE_CONSTRUCTION -> EstrellitaStep.INTRODUCTION // Loop for now
                    }
                }) {
                    Text("Siguiente")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllableConstructionContent(ttsService: TTSService) {
    val syllables = remember { mutableStateListOf("MA", "ME", "MI", "MO", "MU") }
    var formedWord by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Construcción de Sílabas/Palabras",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Display formed word
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = formedWord.ifEmpty { "Forma una palabra aquí" },
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Syllable selection area - using a simple Row for now
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()), // Allow horizontal scrolling if many syllables
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            syllables.forEach { syllable ->
                Card(
                    modifier = Modifier
                        .width(80.dp)
                        .clickable {
                            formedWord += syllable
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = syllable,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                ttsService.speak(formedWord)
            }, enabled = formedWord.isNotEmpty()) {
                Text("Reproducir Palabra")
            }

            Button(onClick = {
                formedWord = "" // Clear the formed word
            }, enabled = formedWord.isNotEmpty()) {
                Text("Borrar")
            }
        }
    }
}
