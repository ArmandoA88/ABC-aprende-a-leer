package com.abcaprende.leer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abcaprende.leer.data.repository.ProgressRepository
import com.abcaprende.leer.services.TTSService
import com.abcaprende.leer.services.VoiceRecognitionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VowelLearningState(
    val currentVowel: String = "A",
    val isPlaying: Boolean = false,
    val isListening: Boolean = false,
    val showFeedback: Boolean = false,
    val feedbackMessage: String = "",
    val feedbackType: FeedbackType = FeedbackType.SUCCESS,
    val score: Int = 0,
    val attempts: Int = 0,
    val correctAttempts: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class FeedbackType {
    SUCCESS, ENCOURAGEMENT, ERROR
}

@HiltViewModel
class VowelLearningViewModel @Inject constructor(
    private val ttsService: TTSService,
    private val voiceService: VoiceRecognitionService,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VowelLearningState())
    val state: StateFlow<VowelLearningState> = _state.asStateFlow()

    private val vowels = listOf("A", "E", "I", "O", "U")
    
    private val successMessages = listOf(
        "¡MUY BIEN! 🎉",
        "¡GENIAL! 👏",
        "¡SÍ! ⭐",
        "¡BRAVO! 🌟",
        "¡WOW! 🎊"
    )
    
    private val encouragementMessages = listOf(
        "¡OTRA VEZ! 💪",
        "¡CASI! 🎯",
        "¡VAMOS! 🔄",
        "¡TÚ PUEDES! 💫",
        "¡INTENTA! 🚀"
    )

    init {
        initializeServices()
    }

    private fun initializeServices() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            try {
                // Intentar inicializar TTS
                val ttsInitialized = ttsService.initialize()
                
                // Intentar inicializar reconocimiento de voz
                val voiceInitialized = voiceService.initialize()
                
                // Continuar aunque algunos servicios fallen
                _state.value = _state.value.copy(isLoading = false)
                
                if (!ttsInitialized) {
                    // Solo mostrar advertencia, no error crítico
                    _state.value = _state.value.copy(
                        showFeedback = true,
                        feedbackMessage = "El sonido puede no funcionar correctamente",
                        feedbackType = FeedbackType.ENCOURAGEMENT
                    )
                }
                
            } catch (e: Exception) {
                // No bloquear la app por errores de servicios
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun setCurrentVowel(vowel: String) {
        _state.value = _state.value.copy(
            currentVowel = vowel.uppercase(),
            attempts = 0,
            correctAttempts = 0,
            score = 0
        )
    }

    fun playVowelSound() {
        if (_state.value.isPlaying) return
        
        _state.value = _state.value.copy(isPlaying = true)
        
        viewModelScope.launch {
            try {
                ttsService.speakLetter(_state.value.currentVowel.lowercase())
                
                // Simular duración del audio
                kotlinx.coroutines.delay(1000)
                
                _state.value = _state.value.copy(isPlaying = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isPlaying = false,
                    error = "Error al reproducir sonido: ${e.message}"
                )
            }
        }
    }

    fun startVoiceRecognition() {
        if (_state.value.isListening) return
        
        _state.value = _state.value.copy(isListening = true)
        
        viewModelScope.launch {
            try {
                if (!voiceService.isAvailable()) {
                    voiceService.initialize()
                }
                
                voiceService.startListening(
                    targetWord = _state.value.currentVowel.lowercase(),
                    callback = { stars, message ->
                        processVoiceResult(stars, message)
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isListening = false,
                    showFeedback = true,
                    feedbackMessage = "No se pudo escuchar. Inténtalo de nuevo.",
                    feedbackType = FeedbackType.ERROR
                )
            }
        }
    }

    private fun processVoiceResult(stars: Int, message: String) {
        val currentState = _state.value
        val newAttempts = currentState.attempts + 1
        
        val (feedbackType, feedbackMessage) = when (stars) {
            3 -> FeedbackType.SUCCESS to successMessages.random()
            2 -> FeedbackType.SUCCESS to "¡Muy bien! Sigue practicando"
            1 -> FeedbackType.ENCOURAGEMENT to "¡Buen intento! Inténtalo otra vez"
            else -> FeedbackType.ENCOURAGEMENT to encouragementMessages.random()
        }
        
        val newScore = if (stars > 0) {
            currentState.score + (stars * 5)
        } else {
            currentState.score
        }
        
        val newCorrectAttempts = if (stars > 0) {
            currentState.correctAttempts + 1
        } else {
            currentState.correctAttempts
        }
        
        _state.value = currentState.copy(
            isListening = false,
            attempts = newAttempts,
            correctAttempts = newCorrectAttempts,
            score = newScore,
            showFeedback = true,
            feedbackMessage = feedbackMessage,
            feedbackType = feedbackType
        )
        
        // Guardar progreso si fue exitoso
        if (stars > 0) {
            saveProgress(currentState.currentVowel, newScore)
        }
    }

    private fun calculateScore(attempts: Int): Int {
        return when (attempts) {
            1 -> 20 // Perfecto en el primer intento
            2 -> 15 // Segundo intento
            3 -> 10 // Tercer intento
            else -> 5 // Más intentos
        }
    }

    private fun saveProgress(vowel: String, score: Int) {
        viewModelScope.launch {
            try {
                // Aquí se guardaría el progreso en la base de datos
                // progressRepository.saveVowelProgress(vowel, score)
            } catch (e: Exception) {
                // Manejar error silenciosamente para no interrumpir la experiencia
            }
        }
    }

    fun nextVowel() {
        val currentIndex = vowels.indexOf(_state.value.currentVowel)
        val nextVowel = if (currentIndex < vowels.size - 1) {
            vowels[currentIndex + 1]
        } else {
            vowels[0] // Volver al principio
        }
        
        setCurrentVowel(nextVowel)
        
        // Reproducir automáticamente la nueva vocal
        playVowelSound()
    }

    fun dismissFeedback() {
        _state.value = _state.value.copy(showFeedback = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun stopAllAudio() {
        ttsService.stop()
        voiceService.stopListening()
        _state.value = _state.value.copy(
            isPlaying = false,
            isListening = false
        )
    }

    fun getVowelColor(vowel: String): androidx.compose.ui.graphics.Color {
        return when (vowel.uppercase()) {
            "A" -> androidx.compose.ui.graphics.Color(0xFFE53E3E)
            "E" -> androidx.compose.ui.graphics.Color(0xFF38A169)
            "I" -> androidx.compose.ui.graphics.Color(0xFF3182CE)
            "O" -> androidx.compose.ui.graphics.Color(0xFFD69E2E)
            "U" -> androidx.compose.ui.graphics.Color(0xFF805AD5)
            else -> androidx.compose.ui.graphics.Color.Gray
        }
    }

    fun getProgressPercentage(): Float {
        val currentState = _state.value
        if (currentState.attempts == 0) return 0f
        
        return (currentState.correctAttempts.toFloat() / currentState.attempts.toFloat()) * 100f
    }

    fun resetProgress() {
        _state.value = _state.value.copy(
            attempts = 0,
            correctAttempts = 0,
            score = 0,
            showFeedback = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopAllAudio()
    }
}
