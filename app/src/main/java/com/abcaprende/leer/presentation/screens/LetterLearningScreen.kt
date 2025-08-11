package com.abcaprende.leer.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.presentation.viewmodels.VowelLearningViewModel
import com.abcaprende.leer.presentation.viewmodels.FeedbackType
import com.abcaprende.leer.ui.theme.*
import android.widget.Toast // Import Toast
import androidx.compose.foundation.gestures.detectTapGestures // Import detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput // Import pointerInput
import kotlinx.coroutines.launch // Import launch
import kotlinx.coroutines.delay // Import delay
import androidx.compose.runtime.rememberCoroutineScope // Import rememberCoroutineScope

@Composable
fun VowelLearningScreen(
    navController: NavController,
    initialVowel: String?, // Changed parameter to be nullable
    viewModel: VowelLearningViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var animationStarted by remember { mutableStateOf(false) }
    val context = LocalContext.current // Get context for Toast

    // Vowels sequence
    val vowels = remember { listOf("A", "E", "I", "O", "U") }
    var currentVowelIndex by remember { mutableStateOf(0) }

    LaunchedEffect(initialVowel) {
        if (initialVowel != null) {
            val index = vowels.indexOf(initialVowel.uppercase())
            if (index != -1) {
                currentVowelIndex = index
            }
        }
        viewModel.setCurrentVowel(vowels[currentVowelIndex])
        animationStarted = true
    }

    // Show "double-click" message on first load
    LaunchedEffect(Unit) {
        Toast.makeText(context, "Puedes avanzar a la siguiente vocal con doble clic en la vocal actual.", Toast.LENGTH_LONG).show()
    }
    
    val vowelScale by animateFloatAsState(
        targetValue = if (state.isPlaying) 1.2f else 1f,
        animationSpec = tween(300),
        label = "vowelScale"
    )
    
    val contentScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = tween(800),
        label = "contentScale"
    )
    
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header simple para niños
            SimpleHeader(
                vowel = state.currentVowel,
                score = state.score
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Vocal principal MÁS GRANDE
            BigVowelDisplay(
                vowel = state.currentVowel,
                isPlaying = state.isPlaying,
                isListening = state.isListening,
                scale = vowelScale,
                onPlaySound = { viewModel.playVowelSound() },
                onDoubleClick = { viewModel.nextVowel() } // Pass the nextVowel function
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Botones MÁS GRANDES y simples
            SimpleBigButtons(
                isPlaying = state.isPlaying,
                isListening = state.isListening,
                onListen = { viewModel.playVowelSound() },
                onRepeat = { viewModel.startVoiceRecognition() },
                onStop = { viewModel.stopVoiceRecognition() },
                onNext = { viewModel.nextVowel() }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Feedback con celebración
            if (state.showFeedback) {
                if (state.feedbackType == FeedbackType.SUCCESS) {
                    CelebrationCard(
                        message = state.feedbackMessage,
                        onDismiss = { viewModel.dismissFeedback() }
                    )
                } else {
                    FeedbackCard(
                        message = state.feedbackMessage,
                        type = state.feedbackType,
                        onDismiss = { viewModel.dismissFeedback() }
                    )
                }
            }
            
            // Error handling
            state.error?.let { error ->
                ErrorCard(
                    message = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Botón volver
            BackButton(
                onClick = { 
                    viewModel.stopAllAudio()
                    navController.popBackStack() 
                }
            )
        }
        
        // Loading indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
private fun VowelLearningHeader(
    vowel: String,
    score: Int,
    attempts: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Título
            Text(
                text = "ESCUCHAR",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        
        // Puntuación
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VowelDisplay(
    vowel: String,
    isPlaying: Boolean,
    isListening: Boolean,
    scale: Float,
    onPlaySound: () -> Unit
) {
    val vowelColor = getVowelColor(vowel)
    
    Card(
        onClick = onPlaySound,
        modifier = Modifier
            .size(200.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Vocal grande
                Text(
                    text = vowel,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 80.sp,
                        color = vowelColor,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                // Indicador de sonido
                if (isPlaying) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔊",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vowel.lowercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = vowelColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                } else {
                    Text(
                        text = "Toca para escuchar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Instrucciones",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "1. Toca la vocal para escuchar su sonido\n" +
                      "2. Presiona 'Escuchar' para repetir\n" +
                      "3. Presiona 'Repetir' y di la vocal\n" +
                      "4. ¡Practica hasta dominarla!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isPlaying: Boolean,
    isListening: Boolean,
    onListen: () -> Unit,
    onRepeat: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón Escuchar
        Button(
            onClick = onListen,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListenColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "🔊",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Escuchar",
                fontWeight = FontWeight.Medium
            )
        }
        
        // Botón Repetir
        Button(
            onClick = onRepeat,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VoiceColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Repetir",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Repetir",
                fontWeight = FontWeight.Medium
            )
        }
        
        // Botón Siguiente
        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Siguiente",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Siguiente",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FeedbackCard(
    message: String,
    type: FeedbackType,
    onDismiss: () -> Unit
) {
    val (backgroundColor, emoji) = when (type) {
        FeedbackType.SUCCESS -> SuccessGreen.copy(alpha = 0.9f) to "🎉"
        FeedbackType.ENCOURAGEMENT -> InfoBlue.copy(alpha = 0.9f) to "💪"
        FeedbackType.ERROR -> ErrorRed.copy(alpha = 0.9f) to "❌"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "OK",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRed.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "OK",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Componente de celebración animada para niños
@Composable
private fun CelebrationCard(
    message: String,
    onDismiss: () -> Unit
) {
    var celebrationScale by remember { mutableStateOf(0.5f) }
    var starRotation by remember { mutableStateOf(0f) }
    
    val scale by animateFloatAsState(
        targetValue = celebrationScale,
        animationSpec = tween(600),
        label = "celebrationScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = starRotation,
        animationSpec = tween(1000),
        label = "starRotation"
    )
    
    LaunchedEffect(Unit) {
        celebrationScale = 1.1f
        starRotation = 360f
        kotlinx.coroutines.delay(4000)
        onDismiss()
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Estrellas animadas
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier
                            .scale(1.2f + (index * 0.1f))
                            .graphicsLayer {
                                rotationZ = rotation + (index * 72f)
                            }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mensaje de celebración MÁS GRANDE
            Text(
                text = message,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Emojis de celebración
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val celebrationEmojis = listOf("🎉", "🎊", "👏", "🌟", "🎈")
                celebrationEmojis.forEach { emoji ->
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.scale(1.3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón OK más grande
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "¡SÍ!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "← VOLVER",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// Componentes simplificados para niños de 2 años
@Composable
private fun SimpleHeader(
    vowel: String,
    score: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Solo mostrar estrellas grandes y coloridas
        repeat(minOf(score / 10, 5)) {
            Text(
                text = "⭐",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BigVowelDisplay(
    vowel: String,
    isPlaying: Boolean,
    isListening: Boolean,
    scale: Float,
    onPlaySound: () -> Unit,
    onDoubleClick: () -> Unit // Add onDoubleClick parameter
) {
    val vowelColor = getVowelColor(vowel)
    
    Card(
        onClick = onPlaySound,
        modifier = Modifier
            .size(280.dp) // MÁS GRANDE para niños pequeños
            .scale(scale)
            .pointerInput(Unit) { // Add pointerInput for double-tap
                detectTapGestures(
                    onTap = { onPlaySound() },
                    onDoubleTap = { onDoubleClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Vocal MUY GRANDE
                Text(
                    text = vowel,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp, // MÁS GRANDE
                        color = vowelColor,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                // Indicador de sonido MÁS SIMPLE
                if (isPlaying) {
                    Text(
                        text = "🔊 ${vowel.lowercase()}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = vowelColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else if (isListening) {
                    // Indicador de grabación MUY OBVIO
                    RecordingIndicator(vowel = vowel)
                } else {
                    Text(
                        text = "👆 Toca aquí",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleBigButtons(
    isPlaying: Boolean,
    isListening: Boolean,
    onListen: () -> Unit,
    onRepeat: () -> Unit,
    onStop: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Botón ESCUCHAR - MÁS GRANDE
        Button(
            onClick = onListen,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp), // MÁS ALTO
            colors = ButtonDefaults.buttonColors(
                containerColor = ListenColor
            ),
            shape = RoundedCornerShape(40.dp), // MÁS REDONDEADO
            enabled = !isPlaying && !isListening
        ) {
            Text(
                text = "🔊",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ESCUCHAR",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        
        // Botón REPETIR/DETENER - MÁS GRANDE
        Button(
            onClick = if (isListening) {
                onStop
            } else {
                onRepeat
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) ErrorRed else VoiceColor
            ),
            shape = RoundedCornerShape(40.dp),
            enabled = !isPlaying
        ) {
            Text(
                text = if (isListening) "🛑" else "🎤",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (isListening) "DETENER" else "DECIR",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        
        // Botón SIGUIENTE - MÁS GRANDE
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen
            ),
            shape = RoundedCornerShape(40.dp)
        ) {
            Text(
                text = "➡️",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "SIGUIENTE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

// Componente de indicador de grabación MUY OBVIO con contador
@Composable
private fun RecordingIndicator(vowel: String) {
    var pulseScale by remember { mutableStateOf(1f) }
    var microphoneRotation by remember { mutableStateOf(0f) }
    var timeLeft by remember { mutableStateOf(15) }
    
    val scale by animateFloatAsState(
        targetValue = pulseScale,
        animationSpec = tween(500),
        label = "pulseScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = microphoneRotation,
        animationSpec = tween(300),
        label = "microphoneRotation"
    )
    
    // Contador de tiempo y animaciones
    LaunchedEffect(Unit) {
        // Iniciar contador de tiempo
        val countdownJob = launch {
            repeat(15) {
                delay(1000)
                timeLeft--
            }
        }
        
        // Animaciones continuas
        while (timeLeft > 0) {
            pulseScale = 1.4f
            microphoneRotation = 15f
            delay(400)
            pulseScale = 1f
            microphoneRotation = -15f
            delay(400)
        }
        
        countdownJob.cancel()
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Contador de tiempo MUY VISIBLE
        Text(
            text = "⏰ $timeLeft",
            style = MaterialTheme.typography.displayMedium.copy(
                color = if (timeLeft <= 3) Color.Red else Color(0xFF2196F3),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.scale(if (timeLeft <= 3) 1.3f else 1f)
        )
        
        // Círculo rojo pulsante de fondo MÁS GRANDE
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    color = Color.Red.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Micrófono animado MÁS GRANDE
            Text(
                text = "🎤",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 80.sp
                ),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = rotation
                    }
            )
        }
        
        // Texto pulsante "GRABANDO" MÁS GRANDE
        Text(
            text = "🔴 GRABANDO",
            style = MaterialTheme.typography.displaySmall.copy(
                color = Color.Red,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.scale(scale)
        )
        
        // Instrucción MUY CLARA
        Text(
            text = "HABLA AHORA",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .background(
                    color = Color.Red.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )
        
        // Instrucción de la vocal
        Text(
            text = "Di: \"${vowel}\"",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = getVowelColor(vowel),
                fontWeight = FontWeight.Bold
            )
        )
        
        // Ondas de sonido animadas MÁS GRANDES
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(7) { index ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(30.dp + (index * 8).dp)
                        .scale(if (scale > 1f) 1.3f else 0.7f)
                        .background(
                            color = Color.Red.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
        
        // Mensaje de ánimo
        Text(
            text = "¡LA APP TE ESTÁ ESCUCHANDO! 👂",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        )
    }
}

// Funciones auxiliares
private fun getVowelColor(vowel: String): Color {
    return when (vowel.uppercase()) {
        "A" -> Color(0xFFE53E3E)
        "E" -> Color(0xFF38A169)
        "I" -> Color(0xFF3182CE)
        "O" -> Color(0xFFD69E2E)
        "U" -> Color(0xFF805AD5)
        else -> Color.Gray
    }
}

private fun getNextVowel(currentVowel: String): String {
    val vowels = listOf("A", "E", "I", "O", "U")
    val currentIndex = vowels.indexOf(currentVowel.uppercase())
    return if (currentIndex < vowels.size - 1) {
        vowels[currentIndex + 1]
    } else {
        vowels[0] // Volver al principio
    }
}

@Preview(showBackground = true)
@Composable
fun VowelLearningScreenPreview() {
    ABCAprendeTheme {
        VowelLearningScreen(
            navController = rememberNavController(),
            initialVowel = "A" // Changed parameter name
        )
    }
}
