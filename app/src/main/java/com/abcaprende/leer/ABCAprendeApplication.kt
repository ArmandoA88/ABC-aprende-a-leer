package com.abcaprende.leer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ABCAprendeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicialización de la aplicación
        initializeApp()
    }
    
    private fun initializeApp() {
        // Configuraciones iniciales si son necesarias
        // Por ejemplo, configurar logging, analytics, etc.
    }
}
