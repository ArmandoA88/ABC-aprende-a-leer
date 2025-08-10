package com.abcaprende.leer.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.ui.theme.*
import androidx.compose.ui.geometry.Offset
import androidx.hilt.navigation.compose.hiltViewModel
import com.abcaprende.leer.presentation.viewmodels.MainViewModel
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import com.abcaprende.leer.presentation.screens.EstrellitaTracingContent // Import the new composable
import com.abcaprende.leer.services.TTSService // Import TTSService
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import kotlinx.coroutines.CoroutineScope // Import CoroutineScope
import kotlinx.coroutines.Dispatchers // Import Dispatchers
import kotlinx.coroutines.launch // Import launch

@Composable
fun TracingScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animationStarted = true
    }
    
    val contentScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = tween(800),
        label = "contentScale"
    )

    val letters = remember { listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "Ñ", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z") }
    var currentLetterIndex by remember { mutableStateOf(0) }
    val currentLetter = letters[currentLetterIndex]

    // State to clear paths in TracingContent
    var clearPathsTrigger by remember { mutableStateOf(false) }

    val context = LocalContext.current // Access LocalContext directly in Composable scope
    val ttsService = remember { TTSService(context) } // Initialize TTSService with context

    DisposableEffect(Unit) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            ttsService.initialize()
        }
        onDispose {
            job.cancel()
            ttsService.shutdown()
        }
    }

    // Speak the letter when it changes
    LaunchedEffect(currentLetter) {
        ttsService.speak(currentLetter)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryGradientStart,
                        PrimaryGradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .scale(contentScale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            TracingHeader()
            
            // Contenido principal
            EstrellitaTracingContent(
                letter = currentLetter,
                ttsService = ttsService,
                clearPathsTrigger = clearPathsTrigger,
                onPathsCleared = { clearPathsTrigger = false }
            )
            
            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        currentLetterIndex = (currentLetterIndex - 1 + letters.size) % letters.size
                        clearPathsTrigger = true
                    },
                    enabled = currentLetterIndex > 0,
                    modifier = Modifier.weight(1f).height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = "← ANTERIOR",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        currentLetterIndex = (currentLetterIndex + 1) % letters.size
                        clearPathsTrigger = true
                    },
                    enabled = currentLetterIndex < letters.size - 1,
                    modifier = Modifier.weight(1f).height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = "SIGUIENTE →",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Botón volver
            BackButton(
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun TracingHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "✏️ RECONOCE Y TRAZA",
            style = MaterialTheme.typography.displaySmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Nivel 2 - Consonantes",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            text = "← VOLVER AL MENÚ",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TracingScreenPreview() {
    ABCAprendeTheme {
        TracingScreen(navController = rememberNavController())
    }
}
