package com.abcaprende.leer.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyllableConstructionViewModel @Inject constructor() : ViewModel() {

    val availableSyllables = mutableStateListOf("MA", "ME", "MI", "MO", "MU")
    val targetWords = mutableStateListOf("MAMA", "MESA", "MIMI")
    private val validWords = setOf("MAMA", "MESA", "MIMI", "MIO", "MIA", "MOMIA") // Example valid words

    var currentWordIndex = mutableStateOf(0)
    var formedWord = mutableStateOf("")
    var feedbackMessage = mutableStateOf("")

    val currentTargetWord: String
        get() = if (currentWordIndex.value < targetWords.size) targetWords[currentWordIndex.value] else ""

    fun addSyllable(syllable: String) {
        formedWord.value += syllable
    }

    fun checkWord(): Boolean {
        val formedWordUpper = formedWord.value.uppercase()
        val currentTargetWordUpper = currentTargetWord.uppercase()

        return if (formedWordUpper == currentTargetWordUpper && validWords.contains(formedWordUpper)) {
            feedbackMessage.value = "¡Correcto!"
            formedWord.value = ""
            currentWordIndex.value++
            if (currentWordIndex.value >= targetWords.size) {
                feedbackMessage.value = "¡Has completado todas las palabras!"
            }
            true
        } else {
            feedbackMessage.value = "Incorrecto, intenta de nuevo."
            formedWord.value = "" // Clear for another try
            false
        }
    }

    fun clearFormedWord() {
        formedWord.value = ""
        feedbackMessage.value = ""
    }

    fun resetActivity() {
        currentWordIndex.value = 0
        formedWord.value = ""
        feedbackMessage.value = ""
    }
}
