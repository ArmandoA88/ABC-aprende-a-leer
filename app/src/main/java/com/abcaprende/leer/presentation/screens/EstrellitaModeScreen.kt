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
import com.abcaprende.leer.presentation.viewmodels.SyllableConstructionViewModel // Import SyllableConstructionViewModel
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.input.pointer.pointerInput // Import pointerInput
import androidx.compose.foundation.gestures.detectDragGestures // Import detectDragGestures
import androidx.compose.ui.geometry.Offset // Import Offset
import androidx.compose.ui.graphics.graphicsLayer // Import graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned // Import onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow // Import boundsInWindow
import androidx.compose.ui.geometry.Rect // Import Rect
import com.abcaprende.leer.presentation.screens.DragTarget // Import DragTarget
import com.abcaprende.leer.presentation.screens.DropTarget // Import DropTarget
import com.abcaprende.leer.presentation.screens.LongPressDraggable // Import LongPressDraggable
import androidx.compose.foundation.Image // Import Image
import androidx.compose.ui.res.painterResource // Import painterResource
import com.abcaprende.leer.R // Import R for drawable resources

enum class EstrellitaStep {
    INTRODUCTION,
    ASSOCIATION,
    TRACING,
    SYLLABLE_CONSTRUCTION
}

data class AssociationItem(val text: String, val imageResId: Int, val isCorrect: Boolean)
data class AssociationActivity(val letter: String, val items: List<AssociationItem>)

@Composable
fun EstrellitaModeScreen(navController: NavController) {
    val context = LocalContext.current
    val ttsService = remember { TTSService(context) }
    val viewModel: VowelLearningViewModel = hiltViewModel()

    val currentLetter = remember { mutableStateOf("M") } // Start with 'M' for example
    var currentStep by remember { mutableStateOf(EstrellitaStep.INTRODUCTION) }
    val coroutineScope = rememberCoroutineScope()

    // Hardcoded association activity for 'M'
    val associationActivityM = remember {
        AssociationActivity(
            letter = "M",
            items = listOf(
                AssociationItem("Mamá", R.drawable.placeholder_image, true),
                AssociationItem("Casa", R.drawable.placeholder_image, false),
                AssociationItem("Mesa", R.drawable.placeholder_image, true),
                AssociationItem("Perro", R.drawable.placeholder_image, false)
            )
        )
    }

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
                    AssociationContent(
                        activity = associationActivityM,
                        ttsService = ttsService,
                        onItemClicked = { item ->
                            // Handle item click feedback
                            if (item.isCorrect) {
                                ttsService.speak("¡Correcto!")
                            } else {
                                ttsService.speak("Intenta de nuevo.")
                            }
                        }
                    )
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
fun AssociationContent(
    activity: AssociationActivity,
    ttsService: TTSService,
    onItemClicked: (AssociationItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Encuentra las palabras que empiezan con '${activity.letter}'",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(activity.items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // Make cards square
                        .clickable {
                            ttsService.speak(item.text) // Speak the item text
                            onItemClicked(item)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.text,
                                modifier = Modifier.size(80.dp) // Adjust size as needed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllableConstructionContent(ttsService: TTSService, viewModel: SyllableConstructionViewModel = hiltViewModel()) {
    val formedWord by viewModel.formedWord
    val feedbackMessage by viewModel.feedbackMessage
    val currentTargetWord = viewModel.currentTargetWord
    val currentWordIndex = viewModel.currentWordIndex.value
    val targetWordsSize = viewModel.targetWords.size

    LongPressDraggable {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Forma la palabra: ${currentTargetWord}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Display formed word and act as drop target
            DropTarget<String>(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onDrop = { syllable ->
                    viewModel.addSyllable(syllable)
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = formedWord.ifEmpty { "Arrastra sílabas aquí" },
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Syllable selection area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                viewModel.availableSyllables.forEach { syllable ->
                    DragTarget(dataToDrop = syllable) {
                        Card(
                            modifier = Modifier
                                .width(80.dp),
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = feedbackMessage,
                color = if (feedbackMessage.contains("Correcto")) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )

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
                    val isCorrect = viewModel.checkWord()
                    if (isCorrect) {
                        ttsService.speak("¡Correcto!")
                        if (currentWordIndex + 1 >= targetWordsSize) { // Check if all words are completed
                            ttsService.speak("¡Has completado todas las palabras!")
                        }
                    } else {
                        ttsService.speak("Incorrecto, intenta de nuevo.")
                    }
                }, enabled = formedWord.isNotEmpty() && currentWordIndex < targetWordsSize) {
                    Text("Comprobar")
                }

                Button(onClick = {
                    viewModel.clearFormedWord()
                }, enabled = formedWord.isNotEmpty()) {
                    Text("Borrar")
                }
            }
        }
    }
}
