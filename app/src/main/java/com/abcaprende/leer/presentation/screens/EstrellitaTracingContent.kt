package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abcaprende.leer.ui.theme.Level2Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import com.abcaprende.leer.services.TTSService

@Composable
fun EstrellitaTracingContent(letter: String, ttsService: TTSService, clearPathsTrigger: Boolean = false, onPathsCleared: () -> Unit = {}) {
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
