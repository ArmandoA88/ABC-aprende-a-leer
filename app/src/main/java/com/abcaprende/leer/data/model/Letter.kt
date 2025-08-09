package com.abcaprende.leer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letters")
data class Letter(
    @PrimaryKey
    val letter: String,
    val words: List<String>,
    val soundEmoji: String,
    val level: Int
)

@Entity(tableName = "letter_progress")
data class LetterProgress(
    @PrimaryKey
    val letter: String,
    val stars: Int = 0,
    val attempts: Int = 0,
    val successes: Int = 0,
    val lastPracticed: Long? = null,
    val mastered: Boolean = false
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey
    val id: Int = 1,
    val totalStars: Int = 0,
    val totalTime: Long = 0,
    val unlockedLevels: List<Int> = listOf(1),
    val achievements: List<String> = emptyList(),
    val sessionStartTime: Long = System.currentTimeMillis()
)

// Datos estáticos de las letras
object LetterData {
    val letters = mapOf(
        // Vocales (Nivel 1)
        "A" to Letter("A", listOf("AVIÓN", "ÁRBOL", "AGUA"), "✈️", 1),
        "E" to Letter("E", listOf("ELEFANTE", "ESTRELLA", "ESCUELA"), "🐘", 1),
        "I" to Letter("I", listOf("IGLÚ", "ISLA", "IMÁN"), "🏠", 1),
        "O" to Letter("O", listOf("OSO", "OJO", "OVEJA"), "🐻", 1),
        "U" to Letter("U", listOf("UVA", "UÑA", "UNICORNIO"), "🍇", 1),
        
        // Consonantes comunes (Nivel 2)
        "B" to Letter("B", listOf("BARCO", "BOLA", "BEBÉ"), "⛵", 2),
        "C" to Letter("C", listOf("COCHE", "CASA", "CAMA"), "🚗", 2),
        "D" to Letter("D", listOf("DELFÍN", "DADO", "DEDO"), "🐬", 2),
        "F" to Letter("F", listOf("FLOR", "FUEGO", "FOCA"), "🌸", 2),
        "G" to Letter("G", listOf("GATO", "GLOBO", "GUITARRA"), "🐱", 2),
        "H" to Letter("H", listOf("HELADO", "HOJA", "HUEVO"), "🍦", 2),
        "J" to Letter("J", listOf("JIRAFA", "JUGO", "JARDÍN"), "🦒", 2),
        "K" to Letter("K", listOf("KIWI", "KARATE", "KOALA"), "🥝", 2),
        "L" to Letter("L", listOf("LEÓN", "LUNA", "LIBRO"), "🦁", 2),
        "M" to Letter("M", listOf("MAMÁ", "MANO", "MESA"), "👩", 2),
        "N" to Letter("N", listOf("NUBE", "NARIZ", "NIDO"), "☁️", 2),
        "Ñ" to Letter("Ñ", listOf("NIÑO", "ÑANDÚ", "CAÑA"), "👶", 2),
        "P" to Letter("P", listOf("PAPÁ", "PELOTA", "PEZ"), "👨", 2),
        "Q" to Letter("Q", listOf("QUESO", "QUETZAL", "QUINOA"), "🧀", 2),
        "R" to Letter("R", listOf("RATÓN", "ROSA", "RELOJ"), "🐭", 2),
        "S" to Letter("S", listOf("SOL", "SAPO", "SILLA"), "☀️", 2),
        "T" to Letter("T", listOf("TIGRE", "TREN", "TORTUGA"), "🐅", 2),
        "V" to Letter("V", listOf("VACA", "VIENTO", "VIOLÍN"), "🐄", 2),
        "W" to Letter("W", listOf("WIFI", "WAFFLE", "WHISKY"), "📶", 2),
        "X" to Letter("X", listOf("XILÓFONO", "TAXI", "BOXEO"), "🎵", 2),
        "Y" to Letter("Y", listOf("YOYO", "YATE", "YOGUR"), "🪀", 2),
        "Z" to Letter("Z", listOf("ZAPATO", "ZORRO", "ZONA"), "👟", 2)
    )
    
    fun getLettersForLevel(level: Int): List<Letter> {
        return letters.values.filter { it.level <= level }
    }
    
    fun getVowels(): List<Letter> {
        return listOf("A", "E", "I", "O", "U").mapNotNull { letters[it] }
    }
    
    fun getConsonants(): List<Letter> {
        return letters.values.filter { it.level == 2 }
    }
}

// Estados de la aplicación
data class AppState(
    val currentLevel: Int = 1,
    val currentLetter: String = "A",
    val totalStars: Int = 0,
    val isLoading: Boolean = false,
    val showCelebration: Boolean = false,
    val celebrationMessage: String = "",
    val celebrationStars: Int = 0
)

// Eventos de la aplicación
sealed class AppEvent {
    object StartLearning : AppEvent()
    object OpenParentPanel : AppEvent()
    data class SelectLevel(val level: Int) : AppEvent()
    data class SelectLetter(val letter: String) : AppEvent()
    data class CompleteActivity(val letter: String, val stars: Int) : AppEvent()
    object DismissCelebration : AppEvent()
    object ResetProgress : AppEvent()
}

// Tipos de mini-juegos
enum class MiniGameType {
    FALLING_LETTERS,
    MEMORY_GAME,
    MATCHING_GAME,
    LETTER_HUNT
}

// Tipos de actividades
enum class ActivityType {
    LISTEN_REPEAT,
    TRACE_LETTER,
    VOICE_RECOGNITION,
    MINI_GAME
}
