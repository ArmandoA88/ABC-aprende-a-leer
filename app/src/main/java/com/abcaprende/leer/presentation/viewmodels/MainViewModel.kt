package com.abcaprende.leer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abcaprende.leer.data.model.AppEvent
import com.abcaprende.leer.data.model.AppState
import com.abcaprende.leer.data.model.Letter
import com.abcaprende.leer.data.model.LetterProgress
import com.abcaprende.leer.data.model.UserProgress
import com.abcaprende.leer.data.repository.ProgressRepository
import com.abcaprende.leer.services.TTSService
import com.abcaprende.leer.services.VoiceRecognitionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val ttsService: TTSService,
    private val voiceService: VoiceRecognitionService
) : ViewModel() {
    
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    private val _letters = MutableStateFlow<List<Letter>>(emptyList())
    val letters: StateFlow<List<Letter>> = _letters.asStateFlow()
    
    private val _letterProgress = MutableStateFlow<List<LetterProgress>>(emptyList())
    val letterProgress: StateFlow<List<LetterProgress>> = _letterProgress.asStateFlow()
    
    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()
    
    init {
        initializeApp()
        observeData()
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            _appState.value = _appState.value.copy(isLoading = true)
            
            try {
                // Inicializar datos de la base de datos
                progressRepository.initializeData()
                
                // Inicializar servicios de forma asíncrona y no bloqueante
                launch {
                    try {
                        ttsService.initialize()
                    } catch (e: Exception) {
                        // TTS no crítico para la funcionalidad básica
                    }
                }
                
                launch {
                    try {
                        voiceService.initialize()
                    } catch (e: Exception) {
                        // Voice recognition no crítico para la funcionalidad básica
                    }
                }
                
                _appState.value = _appState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _appState.value = _appState.value.copy(isLoading = false)
                // Manejar error de inicialización
            }
        }
    }
    
    private fun observeData() {
        viewModelScope.launch {
            // Observar progreso de usuario
            progressRepository.getUserProgressFlow().collect { progress ->
                _userProgress.value = progress
                _appState.value = _appState.value.copy(
                    totalStars = progress?.totalStars ?: 0
                )
            }
        }
        
        viewModelScope.launch {
            // Observar progreso de letras
            progressRepository.getAllProgress().collect { progress ->
                _letterProgress.value = progress
            }
        }
        
        viewModelScope.launch {
            // Observar letras disponibles según el nivel actual
            combine(
                progressRepository.getUserProgressFlow(),
                _appState
            ) { userProgress, appState ->
                val currentLevel = appState.currentLevel
                progressRepository.getLettersForLevel(currentLevel).collect { letters ->
                    _letters.value = letters
                }
            }
        }
    }
    
    fun handleEvent(event: AppEvent) {
        when (event) {
            is AppEvent.StartLearning -> {
                // Navegar a selección de letras
            }
            
            is AppEvent.OpenParentPanel -> {
                // Navegar al panel de padres
            }
            
            is AppEvent.SelectLevel -> {
                _appState.value = _appState.value.copy(currentLevel = event.level)
            }
            
            is AppEvent.SelectLetter -> {
                _appState.value = _appState.value.copy(currentLetter = event.letter)
                speakLetterIntroduction(event.letter)
            }
            
            is AppEvent.CompleteActivity -> {
                completeActivity(event.letter, event.stars)
            }
            
            is AppEvent.DismissCelebration -> {
                _appState.value = _appState.value.copy(
                    showCelebration = false,
                    celebrationMessage = "",
                    celebrationStars = 0
                )
            }
            
            is AppEvent.ResetProgress -> {
                resetProgress()
            }
        }
    }
    
    private fun speakLetterIntroduction(letter: String) {
        viewModelScope.launch {
            val letterData = progressRepository.getLetter(letter)
            if (letterData != null) {
                ttsService.speak("Vamos a aprender la letra $letter")
                kotlinx.coroutines.delay(1500)
                ttsService.speak(letterData.words.first())
            }
        }
    }
    
    private fun completeActivity(letter: String, stars: Int) {
        viewModelScope.launch {
            // Agregar estrellas al progreso
            progressRepository.addStarsToLetter(letter, stars)
            progressRepository.addAttemptToLetter(letter)
            
            // Mostrar celebración
            val message = when (stars) {
                3 -> "¡Excelente trabajo!"
                2 -> "¡Muy bien!"
                1 -> "¡Buen intento!"
                else -> "¡Sigue practicando!"
            }
            
            _appState.value = _appState.value.copy(
                showCelebration = true,
                celebrationMessage = message,
                celebrationStars = stars
            )
            
            // Reproducir sonido de celebración
            if (stars > 0) {
                ttsService.speak(message)
            }
        }
    }
    
    private fun resetProgress() {
        viewModelScope.launch {
            progressRepository.resetAllProgress()
            _appState.value = AppState() // Resetear estado de la app
        }
    }
    
    // Métodos para TTS
    fun speakLetter(letter: String) {
        ttsService.speakLetter(letter)
    }
    
    fun speakWord(word: String) {
        ttsService.speakWord(word)
    }
    
    fun speakSlow(word: String) {
        ttsService.speakSlow(word)
    }
    
    // Métodos para reconocimiento de voz
    fun startVoiceRecognition(targetWord: String, callback: (Int, String) -> Unit) {
        voiceService.startListening(targetWord) { stars, spokenText ->
            callback(stars, spokenText)
            if (stars > 0) {
                completeActivity(_appState.value.currentLetter, stars)
            }
        }
    }
    
    fun stopVoiceRecognition() {
        voiceService.stopListening()
    }
    
    // Métodos para estadísticas
    suspend fun getAccuracyRate(): Int {
        return progressRepository.getAccuracyRate()
    }
    
    suspend fun getMasteredCount(): Int {
        return progressRepository.getMasteredCount()
    }
    
    suspend fun getTotalTime(): Long {
        val userProgress = progressRepository.getUserProgress()
        return if (userProgress != null) {
            System.currentTimeMillis() - userProgress.sessionStartTime
        } else 0
    }
    
    suspend fun exportProgress(): Map<String, Any> {
        return progressRepository.exportProgress()
    }
    
    // Obtener progreso de una letra específica
    suspend fun getLetterProgress(letter: String): LetterProgress? {
        return progressRepository.getProgressForLetter(letter)
    }
    
    // Verificar si un nivel está desbloqueado
    fun isLevelUnlocked(level: Int): Boolean {
        val userProgress = _userProgress.value
        return userProgress?.unlockedLevels?.contains(level) ?: false
    }
    
    // Obtener letras para el nivel actual
    fun getLettersForCurrentLevel(): List<Letter> {
        return _letters.value.filter { it.level <= _appState.value.currentLevel }
    }
    
    // Verificar si una letra está dominada
    fun isLetterMastered(letter: String): Boolean {
        return _letterProgress.value.find { it.letter == letter }?.mastered ?: false
    }
    
    // Obtener estrellas de una letra
    fun getLetterStars(letter: String): Int {
        return _letterProgress.value.find { it.letter == letter }?.stars ?: 0
    }
    
    override fun onCleared() {
        super.onCleared()
        // Limpiar recursos
        ttsService.shutdown()
        voiceService.shutdown()
        
        // Actualizar tiempo total de sesión
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            progressRepository.updateTotalTime(currentTime)
        }
    }
}
