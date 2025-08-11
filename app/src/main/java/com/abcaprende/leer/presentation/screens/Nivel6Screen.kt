package com.abcaprende.leer.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
fun Nivel6Screen(navController: NavController, letra: String?) {
    // Mostramos el refuerzo fonológico para la letra recibida
    // Si no hay letra, volvemos atrás
    if (letra.isNullOrEmpty()) {
        navController.popBackStack()
        return
    }

    PhonologicalAwarenessScreen(
        onNext = {
            // Al terminar el nivel 6 volvemos al flujo principal de Estrellita
            navController.popBackStack()
        },
        letraObjetivo = letra
    )
}
