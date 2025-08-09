package com.abcaprende.leer.di

import android.content.Context
import androidx.room.Room
import com.abcaprende.leer.data.database.AppDatabase
import com.abcaprende.leer.data.database.LetterDao
import com.abcaprende.leer.data.database.LetterProgressDao
import com.abcaprende.leer.data.database.UserProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "abc_aprende_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideLetterDao(database: AppDatabase): LetterDao {
        return database.letterDao()
    }
    
    @Provides
    fun provideLetterProgressDao(database: AppDatabase): LetterProgressDao {
        return database.letterProgressDao()
    }
    
    @Provides
    fun provideUserProgressDao(database: AppDatabase): UserProgressDao {
        return database.userProgressDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    
    @Provides
    @Singleton
    fun provideTTSService(@ApplicationContext context: Context): com.abcaprende.leer.services.TTSService {
        return com.abcaprende.leer.services.TTSService(context)
    }
    
    @Provides
    @Singleton
    fun provideVoiceRecognitionService(@ApplicationContext context: Context): com.abcaprende.leer.services.VoiceRecognitionService {
        return com.abcaprende.leer.services.VoiceRecognitionService(context)
    }
}
