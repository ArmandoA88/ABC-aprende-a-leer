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
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
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
        val similarity = calculateSimilarity(spokenText, targetWord)
        val stars: Int
        val message: String
        
        when {
            similarity >= 0.8 || confidence >= 0.8 -> {
                stars = 3
                message = "¡Perfecto! Dijiste: $spokenText"
            }
            similarity >= 0.6 || confidence >= 0.6 -> {
                stars = 2
                message = "¡Muy bien! Dijiste: $spokenText"
            }
            similarity >= 0.4 || confidence >= 0.4 -> {
                stars = 1
                message = "¡Buen intento! Dijiste: $spokenText"
            }
            else -> {
                stars = 0
                message = "Inténtalo de nuevo. Dijiste: $spokenText"
            }
        }
        
        currentCallback?.invoke(stars, message)
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
