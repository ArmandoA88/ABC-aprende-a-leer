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
import kotlinx.coroutines.flow.update
import android.speech.tts.UtteranceProgressListener // Add this import
import kotlinx.coroutines.launch
import com.abcaprende.leer.presentation.viewmodels.FeedbackType // Add this import
import javax.inject.Inject

@HiltViewModel
class ConsonantLearningViewModel @Inject constructor(
    private val ttsService: TTSService,
    private val voiceService: VoiceRecognitionService,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConsonantLearningState())
    val state: StateFlow<ConsonantLearningState> = _state.asStateFlow()

    private val consonants = listOf(
        "B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "Ñ", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z"
    )
    private var currentConsonantIndex = 0

    init {
        loadInitialConsonant()
        setupTtsListener()
        // Voice recognition listener is handled by the callback in startVoiceRecognition
    }

    private fun loadInitialConsonant() {
        _state.update {
            it.copy(currentConsonant = consonants[currentConsonantIndex])
        }
    }

    fun setCurrentConsonant(consonant: String) {
        val index = consonants.indexOf(consonant.uppercase())
        if (index != -1) {
            currentConsonantIndex = index
            _state.update { it.copy(currentConsonant = consonants[currentConsonantIndex]) }
        }
    }

    fun playConsonantSound() {
        viewModelScope.launch {
            _state.update { it.copy(isPlaying = true) }
            ttsService.speak(_state.value.currentConsonant) // Removed the lambda here
        }
    }

    fun startVoiceRecognition() {
        viewModelScope.launch {
            _state.update { it.copy(isListening = true, error = null) }
            val consonant = _state.value.currentConsonant.lowercase()
            voiceService.startListening(consonant) { stars, message ->
                _state.update { it.copy(isListening = false) }
                processVoiceResult(stars, message)
            }
        }
    }

    private fun processVoiceResult(stars: Int, message: String) {
        val currentConsonant = _state.value.currentConsonant.lowercase()

        if (stars > 0) {
            _state.update {
                it.copy(
                    score = it.score + stars,
                    feedbackType = FeedbackType.SUCCESS,
                    feedbackMessage = message,
                    showFeedback = true
                )
            }
            viewModelScope.launch {
                progressRepository.addStarsToLetter(currentConsonant, stars)
            }
        } else {
            _state.update {
                it.copy(
                    feedbackType = FeedbackType.ERROR,
                    feedbackMessage = message,
                    showFeedback = true
                )
            }
        }
    }

    fun nextConsonant() {
        currentConsonantIndex = (currentConsonantIndex + 1) % consonants.size
        _state.update {
            it.copy(
                currentConsonant = consonants[currentConsonantIndex],
                score = 0, // Reset score for new consonant
                feedbackType = null,
                feedbackMessage = "",
                showFeedback = false
            )
        }
        playConsonantSound()
    }

    fun dismissFeedback() {
        _state.update { it.copy(showFeedback = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun stopAllAudio() {
        ttsService.stop()
        voiceService.stopListening()
        _state.update { it.copy(isPlaying = false, isListening = false) }
    }

    private fun setupTtsListener() {
        ttsService.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Not needed for this logic
            }

            override fun onDone(utteranceId: String?) {
                _state.update { it.copy(isPlaying = false) }
            }

            override fun onError(utteranceId: String?) {
                _state.update { it.copy(isPlaying = false, error = "Error al reproducir audio.") }
            }
        })
    }
}

data class ConsonantLearningState(
    val currentConsonant: String = "",
    val isPlaying: Boolean = false,
    val isListening: Boolean = false,
    val score: Int = 0,
    val feedbackType: FeedbackType? = null,
    val feedbackMessage: String = "",
    val showFeedback: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false // Added for consistency, though not used yet
)
