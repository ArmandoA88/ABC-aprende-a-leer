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

@Composable
fun EstrellitaModeScreen(navController: NavController) {
    val context = LocalContext.current
    val ttsService = remember { TTSService(context) }
    val viewModel: VowelLearningViewModel = hiltViewModel()

    val currentLetter = remember { mutableStateOf("M") } // Start with 'M' for example
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                // For now, just navigate back. Later, this will be "next activity"
                navController.popBackStack()
            }) {
                Text("Volver")
            }
        }
    }
}
