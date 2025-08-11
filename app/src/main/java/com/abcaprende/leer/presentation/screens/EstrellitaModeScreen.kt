package com.abcaprende.leer.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.abcaprende.leer.services.TTSService
import kotlinx.coroutines.launch

data class EstrellitaLetter(
    val letter: String,
    val phrase: String,
    val words: List<String>
)

@Composable
fun EstrellitaModeScreen(navController: NavController) {
    val context = LocalContext.current
    val ttsService = remember { TTSService(context) }
    
    // Lista de letras del programa Estrellita
    val letters = remember {
        listOf(
            EstrellitaLetter("A", "A de avión", listOf("AVIÓN", "ÁRBOL", "ARCO", "BOLA", "CAMA")),
            EstrellitaLetter("B", "B de barco", listOf("BARCO", "BOLA", "BESO", "ÁRBOL", "DADO")),
            EstrellitaLetter("C", "C de casa", listOf("CASA", "CAMA", "CARRO", "BOLA", "FUEGO")),
            EstrellitaLetter("D", "D de dado", listOf("DADO", "DEDO", "DULCE", "GATO", "LUNA")),
            EstrellitaLetter("E", "E de elefante", listOf("ELEFANTE", "ESPEJO", "ENERO", "BOLA", "MESA")),
            EstrellitaLetter("F", "F de fuego", listOf("FUEGO", "FLOTA", "FRESA", "CASA", "PERRO")),
            EstrellitaLetter("G", "G de gato", listOf("GATO", "GOMA", "GUITARRA", "ÁRBOL", "SOL")),
            EstrellitaLetter("H", "H de helado", listOf("HELADO", "HUESO", "HABANA", "CAMA", "LUNA")),
            EstrellitaLetter("I", "I de isla", listOf("ISLA", "IGLESIA", "INVERTIR", "BOLA", "PERRO")),
            EstrellitaLetter("J", "J de jirafa", listOf("JIRAFA", "JUEGO", "JABÓN", "CASA", "FUEGO")),
            EstrellitaLetter("K", "K de koala", listOf("KOALA", "KILO", "KARATE", "ÁRBOL", "LUNA")),
            EstrellitaLetter("L", "L de luna", listOf("LUNA", "LOBO", "LIMÓN", "BOLA", "PERRO")),
            EstrellitaLetter("M", "M de mamá", listOf("MAMÁ", "MESA", "MANO", "SOL", "GATO")),
            EstrellitaLetter("N", "N de nube", listOf("NUBE", "NIÑO", "NARIZ", "CASA", "FUEGO")),
            EstrellitaLetter("O", "O de oso", listOf("OSO", "OJO", "OLIVO", "BOLA", "LUNA")),
            EstrellitaLetter("P", "P de papá", listOf("PAPÁ", "PATO", "PELOTA", "ÁRBOL", "GATO")),
            EstrellitaLetter("Q", "Q de queso", listOf("QUESO", "QUÍMICA", "QUAD", "CASA", "SOL")),
            EstrellitaLetter("R", "R de ratón", listOf("RATÓN", "ROSA", "RANA", "BOLA", "FUEGO")),
            EstrellitaLetter("S", "S de sol", listOf("SOL", "SAPO", "SILLA", "ÁRBOL", "LUNA")),
            EstrellitaLetter("T", "T de tomate", listOf("TOMATE", "TIGRE", "TAZA", "CASA", "PERRO")),
            EstrellitaLetter("U", "U de uva", listOf("UVA", "UNICO", "URBANO", "BOLA", "GATO")),
            EstrellitaLetter("V", "V de vaso", listOf("VASO", "VACA", "VIAJE", "ÁRBOL", "LUNA")),
            EstrellitaLetter("W", "W de wáter", listOf("WÁTER", "WAFLE", "WIND", "CASA", "SOL")),
            EstrellitaLetter("X", "X de xilófono", listOf("XILÓFONO", "XEROX", "XENÓN", "BOLA", "GATO")),
            EstrellitaLetter("Y", "Y de yate", listOf("YATE", "YOGA", "YOGUR", "ÁRBOL", "LUNA")),
            EstrellitaLetter("Z", "Z de zapato", listOf("ZAPATO", "ZORRO", "ZUMO", "CASA", "PERRO"))
        )
    }
    
    var currentLetterIndex by remember { mutableStateOf(0) }
    var currentStep by remember { mutableStateOf("introduction") } // introduction, activity, completed
    var starsEarned by remember { mutableStateOf(0) }
    
    val currentLetter = letters[currentLetterIndex]
    
    val scope = rememberCoroutineScope()
    
    // Inicializar TTS
    DisposableEffect(Unit) {
        scope.launch {
            ttsService.initialize()
        }
        onDispose {
            ttsService.shutdown()
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header con navegación
        EstrellitaHeader(
            currentLetter = currentLetter.letter,
            currentIndex = currentLetterIndex,
            totalLetters = letters.size,
            starsEarned = starsEarned,
            onPrevious = {
                if (currentLetterIndex > 0) {
                    currentLetterIndex--
                    currentStep = "introduction"
                }
            },
            onNext = {
                if (currentLetterIndex < letters.size - 1) {
                    currentLetterIndex++
                    currentStep = "introduction"
                }
            },
            canGoPrevious = currentLetterIndex > 0,
            canGoNext = currentLetterIndex < letters.size - 1,
            onBack = { navController.popBackStack() }
        )
        
        // Contenido principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (currentStep) {
                "introduction" -> {
                    IntroductionStep(
                        letter = currentLetter,
                        ttsService = ttsService,
                        onContinue = { currentStep = "activity" }
                    )
                }
                "activity" -> {
                    ActivityStep(
                        letter = currentLetter,
                        ttsService = ttsService,
                        onComplete = { 
                            currentStep = "completed"
                            starsEarned += 3
                        }
                    )
                }
                "completed" -> {
                    CompletedStep(
                        letter = currentLetter,
                        ttsService = ttsService,
                        onContinue = {
                            if (currentLetterIndex < letters.size - 1) {
                                // Al continuar, vamos a una pantalla de refuerzo (Nivel 6) en lugar de pasar directo a la siguiente letra
                                navController.navigate("nivel6/${currentLetter.letter}")
                            } else {
                                navController.popBackStack()
                            }
                        },
                        onRepeat = { currentStep = "introduction" }
                    )
                }
            }
        }
    }
}

@Composable
fun EstrellitaHeader(
    currentLetter: String,
    currentIndex: Int,
    totalLetters: Int,
    starsEarned: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onBack: () -> Unit
) {
                Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7).copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                
                Text(
                    text = "Estrellita - Letra $currentLetter",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Estrellas",
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        text = starsEarned.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalLetters.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF673AB7)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onPrevious,
                    enabled = canGoPrevious
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Letra anterior")
                }
                
                Text(
                    text = "${currentIndex + 1} de $totalLetters",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                IconButton(
                    onClick = onNext,
                    enabled = canGoNext
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente letra")
                }
            }
        }
    }
}

@Composable
fun IntroductionStep(
    letter: EstrellitaLetter,
    ttsService: TTSService,
    onContinue: () -> Unit
) {
    // Reproducir automáticamente al entrar
    LaunchedEffect(letter.letter) {
        ttsService.speak(letter.phrase)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    ttsService.speak(letter.letter)
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7).copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.letter,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    ttsService.speak(letter.phrase)
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7).copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔊",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = letter.phrase,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¡Empezar Actividades!", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun ActivityStep(
    letter: EstrellitaLetter,
    ttsService: TTSService,
    onComplete: () -> Unit
) {
    var wordsFound by remember { mutableStateOf(0) }
    val requiredWords = 3
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Encuentra las palabras que empiezan con '${letter.letter}'",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7).copy(alpha = 0.1f))
        ) {
            Text(
                text = "Palabras encontradas: $wordsFound/$requiredWords",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF673AB7),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Mostrar palabras como botones
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barajar aleatoriamente las palabras para cada letra
            val shuffledWords = remember(letter) { letter.words.shuffled() }
            shuffledWords.forEach { word ->
                var isClicked by remember { mutableStateOf(false) }
                // Normalizar la palabra para ignorar acentos y determinar si es correcta
                val normalized = java.text.Normalizer.normalize(word, java.text.Normalizer.Form.NFD)
                    .replace("\\p{Mn}".toRegex(), "")
                    .uppercase()
                val target = letter.letter.uppercase()
                val isCorrect = normalized.startsWith(target)
                Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!isClicked) {
                                    ttsService.speak(word)
                                    isClicked = true
                                    if (isCorrect) {
                                        wordsFound++
                                        if (wordsFound >= requiredWords) {
                                            ttsService.speak("¡Excelente! Has encontrado todas las palabras.")
                                            onComplete()
                                        }
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isClicked && isCorrect) {
                                Color.Green.copy(alpha = 0.8f)
                            } else {
                                Color(0xFF673AB7)
                            }
                        )
                    ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isClicked) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Correcto",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = word,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isClicked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔊",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedStep(
    letter: EstrellitaLetter,
    ttsService: TTSService,
    onContinue: () -> Unit,
    onRepeat: () -> Unit
) {
    // Reproducir felicitación automáticamente
    LaunchedEffect(Unit) {
        ttsService.speak("¡Felicidades! Has completado la letra ${letter.letter}. Ganaste tres estrellas.")
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "¡Felicidades!",
            style = MaterialTheme.typography.displayMedium,
            color = Color(0xFF673AB7),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Has completado la letra ${letter.letter}",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Estrella",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "+3",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRepeat,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Repetir", color = Color.White)
            }
            
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Continuar", color = Color.White)
            }
        }
    }
}
