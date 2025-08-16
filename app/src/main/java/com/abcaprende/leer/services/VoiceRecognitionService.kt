package com.abcaprende.leer.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.max

@Singleton
class VoiceRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var currentCallback: ((Int, String) -> Unit)? = null
    private var targetWord: String = ""
    
    fun initialize(): Boolean {
        return try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(recognitionListener)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun startListening(targetWord: String, callback: (Int, String) -> Unit) {
        if (!isAvailable() || isListening) return
        
        this.targetWord = targetWord.lowercase()
        this.currentCallback = callback
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10)
            
            // Configuraciones MUY AGRESIVAS para grabación larga
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 15000L) // 15 segundos
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 12000L) // 12 segundos
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000L) // 2 segundos mínimo
            
            // Configuraciones adicionales para forzar grabación larga
            putExtra("android.speech.extra.DICTATION_MODE", true)
            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
            putExtra("android.speech.extra.GET_AUDIO", true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            
            // Configuraciones experimentales
            putExtra("com.google.android.voicesearch.EXTRA_FORCE_RECOGNITION", true)
            putExtra("com.google.android.voicesearch.EXTRA_ENABLE_BIASING", false)
        }
        
        try {
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            isListening = false
            currentCallback?.invoke(0, "Error al iniciar reconocimiento")
        }
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }
    
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            // El reconocedor está listo para escuchar
        }
        
        override fun onBeginningOfSpeech() {
            // El usuario comenzó a hablar
        }
        
        override fun onRmsChanged(rmsdB: Float) {
            // Cambios en el volumen de la voz (para animaciones)
        }
        
        override fun onBufferReceived(buffer: ByteArray?) {
            // Buffer de audio recibido
        }
        
        override fun onEndOfSpeech() {
            // El usuario terminó de hablar
            isListening = false
        }
        
        override fun onError(error: Int) {
            isListening = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de espera agotado"
                SpeechRecognizer.ERROR_NO_MATCH -> "No se encontró coincidencia"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera de voz agotado"
                else -> "Error desconocido"
            }
            
            currentCallback?.invoke(0, errorMessage)
        }
        
        override fun onResults(results: Bundle?) {
            isListening = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
            
            if (matches != null && matches.isNotEmpty()) {
                val bestMatch = matches[0].lowercase()
                val confidence = confidences?.get(0) ?: 0.5f
                
                evaluateResult(bestMatch, confidence)
            } else {
                currentCallback?.invoke(0, "No se pudo reconocer la voz")
            }
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            // Resultados parciales (opcional para feedback en tiempo real)
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {
            // Eventos adicionales
        }
    }
    
    private fun evaluateResult(spokenText: String, confidence: Float) {
        val stars: Int
        val message: String

        val cleanSpoken = spokenText.lowercase().trim()

        // Usar similitud más flexible para mejorar la detección
        val similarity = calculateSimilarity(cleanSpoken, targetWord)

        if (cleanSpoken == targetWord || similarity >= 0.85 || confidence >= 0.8) {
            stars = 3
            message = "¡PERFECTO! 🌟"
            stopListening()
        } else if (cleanSpoken.contains(targetWord) || similarity >= 0.7 || confidence >= 0.65) {
            stars = 2
            message = "¡MUY BIEN! 👏"
            stopListening()
        } else if (similarity >= 0.5 || confidence >= 0.5) {
            stars = 1
            message = "¡BIEN! Inténtalo otra vez 💪"
        } else {
            stars = 0
            message = "Inténtalo de nuevo 🔄"
        }

        currentCallback?.invoke(stars, message)
    }
    
    private fun calculateVowelSimilarity(spokenText: String, targetVowel: String): Float {
        val spoken = spokenText.lowercase().trim()
        val target = targetVowel.lowercase().trim()
        
        // Verificación directa para vocales
        if (spoken == target) return 1.0f
        
        // Verificar si la vocal está contenida en lo que se dijo
        if (spoken.contains(target)) return 0.8f
        
        // Verificar sonidos similares para cada vocal
        val vowelSimilarities = mapOf(
            "a" to listOf("ah", "aa", "ha"),
            "e" to listOf("eh", "ee", "he"),
            "i" to listOf("ii", "hi", "ee"),
            "o" to listOf("oh", "oo", "ho"),
            "u" to listOf("uu", "hu", "oo")
        )
        
        vowelSimilarities[target]?.forEach { similar ->
            if (spoken.contains(similar) || similar.contains(spoken)) {
                return 0.7f
            }
        }
        
        // Usar algoritmo de distancia como respaldo
        return calculateSimilarity(spoken, target)
    }
    
    private fun calculateSimilarity(str1: String, str2: String): Float {
        val longer = if (str1.length > str2.length) str1 else str2
        val shorter = if (str1.length > str2.length) str2 else str1
        
        if (longer.isEmpty()) return 1.0f
        
        val distance = levenshteinDistance(longer, shorter)
        return (longer.length - distance).toFloat() / longer.length
    }
    
    private fun levenshteinDistance(str1: String, str2: String): Int {
        val matrix = Array(str2.length + 1) { IntArray(str1.length + 1) }
        
        for (i in 0..str2.length) {
            matrix[i][0] = i
        }
        
        for (j in 0..str1.length) {
            matrix[0][j] = j
        }
        
        for (i in 1..str2.length) {
            for (j in 1..str1.length) {
                val cost = if (str2[i - 1] == str1[j - 1]) 0 else 1
                matrix[i][j] = minOf(
                    matrix[i - 1][j] + 1,      // deletion
                    matrix[i][j - 1] + 1,      // insertion
                    matrix[i - 1][j - 1] + cost // substitution
                )
            }
        }
        
        return matrix[str2.length][str1.length]
    }
    
    fun isAvailable(): Boolean {
        return speechRecognizer != null && SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    fun isListening(): Boolean {
        return isListening
    }
    
    fun shutdown() {
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        currentCallback = null
    }
    
    // Método para verificar permisos de micrófono
    fun hasRecordAudioPermission(): Boolean {
        return android.content.pm.PackageManager.PERMISSION_GRANTED == 
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.RECORD_AUDIO
            )
    }
    
    // Método para obtener idiomas soportados
    fun getSupportedLanguages(): List<String> {
        return try {
            val intent = Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS)
            val supportedLanguages = mutableListOf<String>()
            
            // Idiomas comunes de español
            supportedLanguages.addAll(listOf(
                "es-ES", "es-MX", "es-AR", "es-CO", "es-CL", "es-PE", "es-VE"
            ))
            
            supportedLanguages
        } catch (e: Exception) {
            listOf("es-ES", "es-MX")
        }
    }
    
    // Configurar idioma específico
    fun setLanguage(language: String) {
        // Este método se puede usar para cambiar el idioma dinámicamente
        // La implementación dependería de reinicializar el reconocedor con el nuevo idioma
    }
}
