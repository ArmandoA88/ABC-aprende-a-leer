package com.abcaprende.leer.data.database

import androidx.room.*
import com.abcaprende.leer.data.model.Letter
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    
    @Query("SELECT * FROM letters")
    fun getAllLetters(): Flow<List<Letter>>
    
    @Query("SELECT * FROM letters WHERE level <= :level")
    fun getLettersForLevel(level: Int): Flow<List<Letter>>
    
    @Query("SELECT * FROM letters WHERE letter = :letter")
    suspend fun getLetter(letter: String): Letter?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: Letter)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetters(letters: List<Letter>)
    
    @Delete
    suspend fun deleteLetter(letter: Letter)
    
    @Query("DELETE FROM letters")
    suspend fun deleteAllLetters()
}

@Dao
interface LetterProgressDao {
    
    @Query("SELECT * FROM letter_progress")
    fun getAllProgress(): Flow<List<com.abcaprende.leer.data.model.LetterProgress>>
    
    @Query("SELECT * FROM letter_progress WHERE letter = :letter")
    suspend fun getProgressForLetter(letter: String): com.abcaprende.leer.data.model.LetterProgress?
    
    @Query("SELECT * FROM letter_progress WHERE mastered = 1")
    fun getMasteredLetters(): Flow<List<com.abcaprende.leer.data.model.LetterProgress>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: com.abcaprende.leer.data.model.LetterProgress)
    
    @Update
    suspend fun updateProgress(progress: com.abcaprende.leer.data.model.LetterProgress)
    
    @Query("UPDATE letter_progress SET stars = stars + :stars, successes = successes + 1, lastPracticed = :timestamp WHERE letter = :letter")
    suspend fun addStars(letter: String, stars: Int, timestamp: Long)
    
    @Query("UPDATE letter_progress SET attempts = attempts + 1 WHERE letter = :letter")
    suspend fun addAttempt(letter: String)
    
    @Query("UPDATE letter_progress SET mastered = 1 WHERE letter = :letter")
    suspend fun markAsMastered(letter: String)
    
    @Query("DELETE FROM letter_progress")
    suspend fun deleteAllProgress()
}

@Dao
interface UserProgressDao {
    
    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgress(): com.abcaprende.leer.data.model.UserProgress?
    
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgressFlow(): Flow<com.abcaprende.leer.data.model.UserProgress?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: com.abcaprende.leer.data.model.UserProgress)
    
    @Update
    suspend fun updateUserProgress(progress: com.abcaprende.leer.data.model.UserProgress)
    
    @Query("UPDATE user_progress SET totalStars = totalStars + :stars WHERE id = 1")
    suspend fun addTotalStars(stars: Int)
    
    @Query("UPDATE user_progress SET totalTime = :time WHERE id = 1")
    suspend fun updateTotalTime(time: Long)
    
    @Query("DELETE FROM user_progress")
    suspend fun deleteUserProgress()
}
