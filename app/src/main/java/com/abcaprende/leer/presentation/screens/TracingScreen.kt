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

    // Speak the letter when it changes
    LaunchedEffect(currentLetter) {
        mainViewModel.speakLetter(currentLetter)
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
            TracingContent(
                letter = currentLetter,
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
private fun TracingContent(letter: String, clearPathsTrigger: Boolean, onPathsCleared: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp), // Keep padding for the overall card content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val paths = remember { mutableStateListOf<Path>() }
            var currentPath by remember { mutableStateOf(Path()) }

            // Clear paths when trigger is true
            LaunchedEffect(clearPathsTrigger) {
                if (clearPathsTrigger) {
                    paths.clear()
                    onPathsCleared()
                }
            }

            val textMeasurer = rememberTextMeasurer()
            val textStyle = MaterialTheme.typography.displayLarge.copy(
                fontSize = 400.sp, // Increased font size
                color = Color.Black.copy(alpha = 0.2f),
                fontWeight = FontWeight.Bold
            )

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                paths.add(currentPath)
                            },
                            onDragEnd = {
                                // Optionally, you can process the path here (e.g., for recognition)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Create a new Path object that includes the new point
                                // This forces recomposition
                                currentPath = Path().apply {
                                    addPath(currentPath) // Copy existing path segments
                                    lineTo(change.position.x, change.position.y) // Add new point
                                }
                                // Update the last path in the list to the new path object
                                // This is crucial for the Canvas to redraw the updated path
                                paths[paths.lastIndex] = currentPath
                            }
                        )
                    }
            ) {
                // Draw the faint letter as a background
                val textLayoutResult = textMeasurer.measure(
                    text = letter,
                    style = textStyle,
                    constraints = Constraints.fixedWidth(size.width.toInt())
                )
                val textX = (size.width - textLayoutResult.size.width) / 2f
                val textY = (size.height - textLayoutResult.size.height) / 2f
                drawText(
                    textLayoutResult = textLayoutResult,
                    color = textStyle.color,
                    topLeft = Offset(textX, textY)
                )

                // Draw the user's paths
                paths.forEach { drawnPath ->
                    drawPath(
                        path = drawnPath,
                        color = Color.Blue,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { paths.clear() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Level2Color
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "BORRAR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
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
