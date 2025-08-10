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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abcaprende.leer.data.model.AppEvent
import com.abcaprende.leer.presentation.viewmodels.MainViewModel
import com.abcaprende.leer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val appState by viewModel.appState.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    
    var animationStarted by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        animationStarted = true
    }
    
    val titleScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = tween(1000),
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con título y estrellas
            HomeHeader(
                totalStars = appState.totalStars,
                titleScale = titleScale
            )
            
            // Selector de niveles
            LevelSelector(
                currentLevel = appState.currentLevel,
                unlockedLevels = userProgress?.unlockedLevels ?: listOf(1),
                onLevelSelected = { level ->
                    viewModel.handleEvent(AppEvent.SelectLevel(level))
                }
            )
            
            // Botones principales
            MainButtons(
                onStartLearning = {
                    viewModel.handleEvent(AppEvent.StartLearning)
                    navController.navigate("letter_selection")
                },
                onOpenParentPanel = {
                    viewModel.handleEvent(AppEvent.OpenParentPanel)
                    navController.navigate("parent")
                }
            )
        }
        
        // Mostrar loading si es necesario
        if (appState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    totalStars: Int,
    titleScale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 32.dp)
    ) {
        // Título principal
        Text(
            text = "🎯 ABC Aprende a Leer",
            style = MaterialTheme.typography.displayMedium.copy(
                color = Color.White
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(titleScale)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contador de estrellas
        StarsDisplay(totalStars = totalStars)
    }
}

@Composable
private fun StarsDisplay(totalStars: Int) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = totalStars.toString(),
                style = StatsNumberStyle.copy(
                    color = Color.White,
                    fontSize = 24.sp
                )
            )
        }
    }
}

@Composable
private fun LevelSelector(
    currentLevel: Int,
    unlockedLevels: List<Int>,
    onLevelSelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Elige tu nivel",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LevelButton(
                level = 1,
                title = "Escucha y Repite",
                description = "Vocales",
                isUnlocked = unlockedLevels.contains(1),
                isSelected = currentLevel == 1,
                color = Level1Color,
                onClick = { onLevelSelected(1) }
            )
            
            LevelButton(
                level = 2,
                title = "Reconoce y Traza",
                description = "Consonantes",
                isUnlocked = unlockedLevels.contains(2),
                isSelected = currentLevel == 2,
                color = Level2Color,
                onClick = { onLevelSelected(2) }
            )
            
            LevelButton(
                level = 3,
                title = "Forma Palabras",
                description = "Sílabas",
                isUnlocked = unlockedLevels.contains(3),
                isSelected = currentLevel == 3,
                color = Level3Color,
                onClick = { onLevelSelected(3) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LevelButton(
    level: Int,
    title: String,
    description: String,
    isUnlocked: Boolean,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = if (isUnlocked) onClick else { {} },
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                color.copy(alpha = 0.3f)
            } else {
                Color.White.copy(alpha = if (isUnlocked) 0.1f else 0.05f)
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, color)
        } else null
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
                        color = if (isUnlocked) color else Color.Gray,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) level.toString() else "🔒",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del nivel
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
private fun MainButtons(
    onStartLearning: () -> Unit,
    onOpenParentPanel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón principal - Empezar a jugar
        Button(
            onClick = onStartLearning,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SecondaryGradientStart,
                                SecondaryGradientEnd
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎮 ¡Empezar a Jugar!",
                    style = ButtonTextStyle.copy(
                        color = Color.White
                    )
                )
            }
        }
        
        // Botón secundario - Panel de padres
        OutlinedButton(
            onClick = onOpenParentPanel,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                2.dp, 
                Color.White.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "👨‍👩‍👧‍👦 Panel de Padres",
                style = ButtonTextStyle.copy(
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ABCAprendeTheme {
        HomeScreen(
            navController = rememberNavController(),
            viewModel = MainViewModel(
                progressRepository = TODO(),
                ttsService = TODO(),
                voiceService = TODO()
            )
        )
    }
}
