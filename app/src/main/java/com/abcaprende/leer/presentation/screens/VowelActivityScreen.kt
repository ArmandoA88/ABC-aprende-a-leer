package com.abcaprende.leer.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun VowelActivityScreen(
    navController: NavController,
    vowel: String
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animationStarted = true
    }
    
    val iconScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.5f,
        animationSpec = tween(1000),
        label = "iconScale"
    )
    
    val contentScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = tween(800),
        label = "contentScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de construcción animado
            Text(
                text = "🚧",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 120.sp
                ),
                modifier = Modifier.scale(iconScale)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Contenido principal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(contentScale)
            ) {
                Text(
                    text = "Selección de Letras",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Próximamente",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mostrar la vocal seleccionada
                if (vowel.isNotEmpty()) {
                    Text(
                        text = "Vocal: $vowel",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Level1Color,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Botón Volver con estilo púrpura
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9C27B0),
                                    Color(0xFF673AB7)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Volver",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VowelActivityScreenPreview() {
    ABCAprendeTheme {
        VowelActivityScreen(
            navController = rememberNavController(),
            vowel = "A"
        )
    }
}
