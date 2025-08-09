package com.abcaprende.leer.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TTSService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "ES"))
                    ?: tts?.setLanguage(Locale("es", "MX"))
                    ?: tts?.setLanguage(Locale("es"))
                
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Usar idioma por defecto si español no está disponible
                    tts?.setLanguage(Locale.getDefault())
                }
                
                // Configurar parámetros de voz
                tts?.setPitch(1.2f) // Tono más alto para niños
                tts?.setSpeechRate(0.9f) // Velocidad ligeramente más lenta
                
                isInitialized = true
                continuation.resume(true)
            } else {
                isInitialized = false
                continuation.resume(false)
            }
        }
    }
    
    fun speak(text: String, rate: Float = 0.9f) {
        if (!isInitialized || tts == null) return
        
        tts?.setSpeechRate(rate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
    }
    
    fun speakLetter(letter: String) {
        speak(letter, 0.8f)
    }
    
    fun speakWord(word: String) {
        speak(word, 0.9f)
    }
    
    fun speakSlow(text: String) {
        // Separar por sílabas de manera simple para español
        val syllables = splitIntoSyllables(text)
        
        syllables.forEachIndexed { index, syllable ->
            val delay = index * 800L // 800ms entre sílabas
            
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                speak(syllable, 0.6f)
            }, delay)
        }
    }
    
    private fun splitIntoSyllables(word: String): List<String> {
        // Implementación simple de separación silábica para español
        val vowels = "aeiouáéíóúAEIOUÁÉÍÓÚ"
        val syllables = mutableListOf<String>()
        var currentSyllable = ""
        
        for (i in word.indices) {
            currentSyllable += word[i]
            
            if (vowels.contains(word[i])) {
                // Si la siguiente letra es consonante o es el final
                if (i == word.length - 1 || !vowels.contains(word[i + 1])) {
                    syllables.add(currentSyllable)
                    currentSyllable = ""
                }
            }
        }
        
        if (currentSyllable.isNotEmpty()) {
            syllables.add(currentSyllable)
        }
        
        return if (syllables.isNotEmpty()) syllables else listOf(word)
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
    
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }
    
    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        tts?.setOnUtteranceProgressListener(listener)
    }
    
    // Método para obtener voces disponibles
    fun getAvailableVoices(): Set<android.speech.tts.Voice>? {
        return tts?.voices
    }
    
    // Método para establecer una voz específica
    fun setVoice(voice: android.speech.tts.Voice): Int? {
        return tts?.setVoice(voice)
    }
    
    // Verificar si TTS está disponible
    fun isAvailable(): Boolean {
        return isInitialized && tts != null
    }
}
