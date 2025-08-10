package com.abcaprende.leer.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.ui.theme.*

data class Vowel(
    val letter: String,
    val sound: String,
    val color: Color,
    val isUnlocked: Boolean = true
)

@Composable
fun VowelSelectionScreen(
    navController: NavController
) {
    val vowels = listOf(
        Vowel("A", "a", Color(0xFFE53E3E)),
        Vowel("E", "e", Color(0xFF38A169)),
        Vowel("I", "i", Color(0xFF3182CE)),
        Vowel("O", "o", Color(0xFFD69E2E)),
        Vowel("U", "u", Color(0xFF805AD5))
    )
    
    var animationStarted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animationStarted = true
    }
    
    val titleScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = tween(800),
        label = "titleScale"
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            VowelSelectionHeader(titleScale = titleScale)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Grid de vocales MÁS GRANDE para niños pequeños
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(vowels) { vowel ->
                    BigVowelCard(
                        vowel = vowel,
                        onClick = {
                            navController.navigate("vowel_activity/${vowel.letter}")
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón volver
            BackButton(
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun VowelSelectionHeader(titleScale: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Elige tu nivel",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nivel 1 card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Level1Color.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Número del nivel
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Level1Color,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Información del nivel
                Column {
                    Text(
                        text = "Escucha y Repite",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Vocales",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VowelCard(
    vowel: Vowel,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "cardScale"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Círculo con la vocal
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = vowel.color,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vowel.letter,
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Sonido de la vocal
                Text(
                    text = vowel.sound,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = vowel.color,
                        fontWeight = FontWeight.Medium
                    )
                )
                
                if (!vowel.isUnlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔒",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

// Tarjeta de vocal MÁS GRANDE para niños de 2 años
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BigVowelCard(
    vowel: Vowel,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(150),
        label = "cardScale"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        shape = RoundedCornerShape(24.dp) // MÁS REDONDEADO
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Círculo con la vocal MÁS GRANDE
                Box(
                    modifier = Modifier
                        .size(100.dp) // MÁS GRANDE
                        .background(
                            color = vowel.color,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vowel.letter,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp, // MÁS GRANDE
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sonido de la vocal MÁS GRANDE
                Text(
                    text = vowel.sound,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp, // MÁS GRANDE
                        color = vowel.color,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                if (!vowel.isUnlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🔒",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp), // MÁS GRANDE
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Text(
            text = "← Volver",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VowelSelectionScreenPreview() {
    ABCAprendeTheme {
        VowelSelectionScreen(navController = rememberNavController())
    }
}
