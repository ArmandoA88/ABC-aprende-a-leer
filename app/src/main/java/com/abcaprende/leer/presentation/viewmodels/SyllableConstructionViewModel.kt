package com.abcaprende.leer.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyllableConstructionViewModel @Inject constructor() : ViewModel() {

    val availableSyllables = mutableStateListOf<String>()
    val targetWords = mutableStateListOf<String>()
    private var validWords = setOf<String>()

    var currentWordIndex = mutableStateOf(0)
    var formedWord = mutableStateOf("")
    var feedbackMessage = mutableStateOf("")
    var isCompleted = mutableStateOf(false)

    val currentTargetWord: String
        get() = if (currentWordIndex.value < targetWords.size) targetWords[currentWordIndex.value] else ""

    fun initializeForLetter(syllables: List<String>, words: List<String>) {
        availableSyllables.clear()
        availableSyllables.addAll(syllables)
        
        targetWords.clear()
        targetWords.addAll(words)
        
        // Crear palabras válidas basadas en las sílabas disponibles
        validWords = generateValidWords(syllables) + words.toSet()
        
        resetActivity()
    }

    private fun generateValidWords(syllables: List<String>): Set<String> {
        val validWordsSet = mutableSetOf<String>()
        
        // Generar combinaciones simples de 2 sílabas
        for (i in syllables.indices) {
            for (j in syllables.indices) {
                val word = syllables[i] + syllables[j]
                validWordsSet.add(word)
            }
        }
        
        return validWordsSet
    }

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
                isCompleted.value = true
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
        isCompleted.value = false
    }
    
    fun hasMoreWords(): Boolean {
        return currentWordIndex.value < targetWords.size
    }
}
