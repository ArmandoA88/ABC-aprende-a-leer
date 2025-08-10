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
import com.abcaprende.leer.presentation.viewmodels.ConsonantLearningViewModel
import com.abcaprende.leer.presentation.viewmodels.FeedbackType
import com.abcaprende.leer.ui.theme.*
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun ConsonantLearningScreen(
    navController: NavController,
    initialConsonant: String?,
    viewModel: ConsonantLearningViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var animationStarted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(initialConsonant) {
        if (initialConsonant != null) {
            viewModel.setCurrentConsonant(initialConsonant)
        }
        animationStarted = true
    }

    LaunchedEffect(Unit) {
        Toast.makeText(context, "Puedes avanzar a la siguiente consonante con doble clic en la consonante actual.", Toast.LENGTH_LONG).show()
    }
    
    val consonantScale by animateFloatAsState(
        targetValue = if (state.isPlaying) 1.2f else 1f,
        animationSpec = tween(300),
        label = "consonantScale"
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
            SimpleHeader(
                consonant = state.currentConsonant,
                score = state.score
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            BigConsonantDisplay(
                consonant = state.currentConsonant,
                isPlaying = state.isPlaying,
                isListening = state.isListening,
                scale = consonantScale,
                onPlaySound = { viewModel.playConsonantSound() },
                onDoubleClick = { viewModel.nextConsonant() }
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            SimpleBigButtons(
                isPlaying = state.isPlaying,
                isListening = state.isListening,
                onListen = { viewModel.playConsonantSound() },
                onRepeat = { viewModel.startVoiceRecognition() },
                onNext = { viewModel.nextConsonant() }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (state.showFeedback) {
                if (state.feedbackType == FeedbackType.SUCCESS) {
                    CelebrationCard(
                        message = state.feedbackMessage,
                        onDismiss = { viewModel.dismissFeedback() }
                    )
                } else {
                    FeedbackCard(
                        message = state.feedbackMessage,
                        type = state.feedbackType!!,
                        onDismiss = { viewModel.dismissFeedback() }
                    )
                }
            }
            
            state.error?.let { error ->
                ErrorCard(
                    message = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            BackButton(
                onClick = { 
                    viewModel.stopAllAudio()
                    navController.popBackStack() 
                }
            )
        }
        
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
private fun SimpleHeader(
    consonant: String,
    score: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
private fun BigConsonantDisplay(
    consonant: String,
    isPlaying: Boolean,
    isListening: Boolean,
    scale: Float,
    onPlaySound: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val consonantColor = getConsonantColor(consonant)
    
    Card(
        onClick = onPlaySound,
        modifier = Modifier
            .size(280.dp)
            .scale(scale)
            .pointerInput(Unit) {
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
                Text(
                    text = consonant,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp,
                        color = consonantColor,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                if (isPlaying) {
                    Text(
                        text = "🔊 ${consonant.lowercase()}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = consonantColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else if (isListening) {
                    Text(
                        text = "🎤 Habla",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = VoiceColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
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
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Button(
            onClick = onListen,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListenColor
            ),
            shape = RoundedCornerShape(40.dp),
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
        
        Button(
            onClick = onRepeat,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VoiceColor
            ),
            shape = RoundedCornerShape(40.dp),
            enabled = !isPlaying && !isListening
        ) {
            Text(
                text = "🎤",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (isListening) "OYE..." else "DECIR",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        
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
            
            Text(
                text = message,
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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

private fun getConsonantColor(consonant: String): Color {
    // Using Level2Color for all consonants for now
    return Level2Color
}

@Preview(showBackground = true)
@Composable
fun ConsonantLearningScreenPreview() {
    ABCAprendeTheme {
        ConsonantLearningScreen(
            navController = rememberNavController(),
            initialConsonant = "B"
        )
    }
}
