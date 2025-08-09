package com.abcaprende.leer.data.repository

import com.abcaprende.leer.data.database.LetterDao
import com.abcaprende.leer.data.database.LetterProgressDao
import com.abcaprende.leer.data.database.UserProgressDao
import com.abcaprende.leer.data.model.Letter
import com.abcaprende.leer.data.model.LetterData
import com.abcaprende.leer.data.model.LetterProgress
import com.abcaprende.leer.data.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val letterDao: LetterDao,
    private val letterProgressDao: LetterProgressDao,
    private val userProgressDao: UserProgressDao
) {
    
    // Inicializar datos si es la primera vez
    suspend fun initializeData() {
        val existingLetters = letterDao.getAllLetters().first()
        if (existingLetters.isEmpty()) {
            // Insertar letras iniciales
            letterDao.insertLetters(LetterData.letters.values.toList())
            
            // Crear progreso inicial para cada letra
            LetterData.letters.keys.forEach { letter ->
                letterProgressDao.insertProgress(LetterProgress(letter = letter))
            }
            
            // Crear progreso de usuario inicial
            userProgressDao.insertUserProgress(UserProgress())
        }
    }
    
    // Métodos para letras
    fun getAllLetters(): Flow<List<Letter>> = letterDao.getAllLetters()
    
    fun getLettersForLevel(level: Int): Flow<List<Letter>> = letterDao.getLettersForLevel(level)
    
    suspend fun getLetter(letter: String): Letter? = letterDao.getLetter(letter)
    
    // Métodos para progreso de letras
    fun getAllProgress(): Flow<List<LetterProgress>> = letterProgressDao.getAllProgress()
    
    suspend fun getProgressForLetter(letter: String): LetterProgress? = 
        letterProgressDao.getProgressForLetter(letter)
    
    fun getMasteredLetters(): Flow<List<LetterProgress>> = letterProgressDao.getMasteredLetters()
    
    suspend fun addStarsToLetter(letter: String, stars: Int) {
        val timestamp = System.currentTimeMillis()
        letterProgressDao.addStars(letter, stars, timestamp)
        
        // Verificar si la letra está dominada (3+ estrellas)
        val progress = letterProgressDao.getProgressForLetter(letter)
        if (progress != null && progress.stars + stars >= 3) {
            letterProgressDao.markAsMastered(letter)
        }
        
        // Actualizar estrellas totales del usuario
        userProgressDao.addTotalStars(stars)
        
        // Verificar desbloqueo de niveles
        checkLevelUnlock()
    }
    
    suspend fun addAttemptToLetter(letter: String) {
        letterProgressDao.addAttempt(letter)
    }
    
    // Métodos para progreso de usuario
    suspend fun getUserProgress(): UserProgress? = userProgressDao.getUserProgress()
    
    fun getUserProgressFlow(): Flow<UserProgress?> = userProgressDao.getUserProgressFlow()
    
    suspend fun updateUserProgress(progress: UserProgress) {
        userProgressDao.updateUserProgress(progress)
    }
    
    suspend fun updateTotalTime(time: Long) {
        userProgressDao.updateTotalTime(time)
    }
    
    // Verificar desbloqueo de niveles
    private suspend fun checkLevelUnlock() {
        val userProgress = getUserProgress() ?: return
        val masteredLetters = getMasteredLetters().first()
        
        val vowelsMastered = masteredLetters.count { letter ->
            listOf("A", "E", "I", "O", "U").contains(letter.letter)
        }
        
        val consonantsMastered = masteredLetters.count { letter ->
            !listOf("A", "E", "I", "O", "U").contains(letter.letter)
        }
        
        val newUnlockedLevels = userProgress.unlockedLevels.toMutableList()
        
        // Desbloquear nivel 2 si se dominan 3 vocales
        if (vowelsMastered >= 3 && !newUnlockedLevels.contains(2)) {
            newUnlockedLevels.add(2)
        }
        
        // Desbloquear nivel 3 si se dominan 10 consonantes
        if (consonantsMastered >= 10 && !newUnlockedLevels.contains(3)) {
            newUnlockedLevels.add(3)
        }
        
        if (newUnlockedLevels != userProgress.unlockedLevels) {
            updateUserProgress(userProgress.copy(unlockedLevels = newUnlockedLevels))
        }
    }
    
    // Calcular estadísticas
    suspend fun getAccuracyRate(): Int {
        val allProgress = getAllProgress().first()
        val totalAttempts = allProgress.sumOf { it.attempts }
        val totalSuccesses = allProgress.sumOf { it.successes }
        
        return if (totalAttempts > 0) {
            ((totalSuccesses.toFloat() / totalAttempts) * 100).toInt()
        } else 0
    }
    
    suspend fun getMasteredCount(): Int {
        return getMasteredLetters().first().size
    }
    
    // Reiniciar progreso
    suspend fun resetAllProgress() {
        letterProgressDao.deleteAllProgress()
        userProgressDao.deleteUserProgress()
        
        // Reinicializar datos
        LetterData.letters.keys.forEach { letter ->
            letterProgressDao.insertProgress(LetterProgress(letter = letter))
        }
        userProgressDao.insertUserProgress(UserProgress())
    }
    
    // Exportar progreso
    suspend fun exportProgress(): Map<String, Any> {
        val userProgress = getUserProgress()
        val letterProgress = getAllProgress().first()
        
        return mapOf(
            "exportDate" to System.currentTimeMillis(),
            "userProgress" to userProgress,
            "letterProgress" to letterProgress,
            "masteredCount" to getMasteredCount(),
            "accuracyRate" to getAccuracyRate()
        )
    }
}
