package com.abcaprende.leer.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.abcaprende.leer.R

data class LetterData(
    val letter: String,
    val phonicPhrase: String,
    val associationItems: List<AssociationItem>,
    val syllables: List<String>,
    val targetWords: List<String>
)

data class AssociationItem(
    val text: String,
    val imageResId: Int,
    val isCorrect: Boolean
)

@HiltViewModel
class EstrellitaModeViewModel @Inject constructor() : ViewModel() {
    
    // Lista completa de letras del programa Estrellita
    private val lettersData = listOf(
        LetterData(
            letter = "M",
            phonicPhrase = "M de mamá",
            associationItems = listOf(
                AssociationItem("Mamá", R.drawable.ic_mama, true),
                AssociationItem("Casa", R.drawable.ic_casa, false),
                AssociationItem("Mesa", R.drawable.ic_mesa, true),
                AssociationItem("Perro", R.drawable.placeholder_image, false),
                AssociationItem("Mano", R.drawable.placeholder_image, true),
                AssociationItem("Gato", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("MA", "ME", "MI", "MO", "MU"),
            targetWords = listOf("MAMA", "MESA", "MIMI", "MEMO")
        ),
        LetterData(
            letter = "P",
            phonicPhrase = "P de papá",
            associationItems = listOf(
                AssociationItem("Papá", R.drawable.placeholder_image, true),
                AssociationItem("Mesa", R.drawable.ic_mesa, false),
                AssociationItem("Pato", R.drawable.placeholder_image, true),
                AssociationItem("Casa", R.drawable.ic_casa, false),
                AssociationItem("Pelota", R.drawable.placeholder_image, true),
                AssociationItem("Mamá", R.drawable.ic_mama, false)
            ),
            syllables = listOf("PA", "PE", "PI", "PO", "PU"),
            targetWords = listOf("PAPA", "PEPE", "PIPA", "POPO")
        ),
        LetterData(
            letter = "S",
            phonicPhrase = "S de sol",
            associationItems = listOf(
                AssociationItem("Sol", R.drawable.ic_sol, true),
                AssociationItem("Pato", R.drawable.placeholder_image, false),
                AssociationItem("Sapo", R.drawable.placeholder_image, true),
                AssociationItem("Mesa", R.drawable.ic_mesa, false),
                AssociationItem("Silla", R.drawable.placeholder_image, true),
                AssociationItem("Papá", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("SA", "SE", "SI", "SO", "SU"),
            targetWords = listOf("SASA", "SESO", "SISO", "SUSU")
        ),
        LetterData(
            letter = "L",
            phonicPhrase = "L de luna",
            associationItems = listOf(
                AssociationItem("Luna", R.drawable.placeholder_image, true),
                AssociationItem("Sol", R.drawable.placeholder_image, false),
                AssociationItem("Lobo", R.drawable.placeholder_image, true),
                AssociationItem("Sapo", R.drawable.placeholder_image, false),
                AssociationItem("Limón", R.drawable.placeholder_image, true),
                AssociationItem("Silla", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("LA", "LE", "LI", "LO", "LU"),
            targetWords = listOf("LALA", "LELO", "LILI", "LULU")
        ),
        LetterData(
            letter = "T",
            phonicPhrase = "T de tomate",
            associationItems = listOf(
                AssociationItem("Tomate", R.drawable.placeholder_image, true),
                AssociationItem("Luna", R.drawable.placeholder_image, false),
                AssociationItem("Tigre", R.drawable.placeholder_image, true),
                AssociationItem("Lobo", R.drawable.placeholder_image, false),
                AssociationItem("Taza", R.drawable.placeholder_image, true),
                AssociationItem("Limón", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("TA", "TE", "TI", "TO", "TU"),
            targetWords = listOf("TATA", "TETE", "TITI", "TOTO")
        ),
        LetterData(
            letter = "N",
            phonicPhrase = "N de nube",
            associationItems = listOf(
                AssociationItem("Nube", R.drawable.placeholder_image, true),
                AssociationItem("Tomate", R.drawable.placeholder_image, false),
                AssociationItem("Niño", R.drawable.placeholder_image, true),
                AssociationItem("Tigre", R.drawable.placeholder_image, false),
                AssociationItem("Nariz", R.drawable.placeholder_image, true),
                AssociationItem("Taza", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("NA", "NE", "NI", "NO", "NU"),
            targetWords = listOf("NANA", "NENE", "NINI", "NONO")
        ),
        LetterData(
            letter = "D",
            phonicPhrase = "D de dado",
            associationItems = listOf(
                AssociationItem("Dado", R.drawable.placeholder_image, true),
                AssociationItem("Nube", R.drawable.placeholder_image, false),
                AssociationItem("Dedo", R.drawable.placeholder_image, true),
                AssociationItem("Niño", R.drawable.placeholder_image, false),
                AssociationItem("Dulce", R.drawable.placeholder_image, true),
                AssociationItem("Nariz", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("DA", "DE", "DI", "DO", "DU"),
            targetWords = listOf("DADA", "DEDO", "DIDI", "DODO")
        ),
        LetterData(
            letter = "R",
            phonicPhrase = "R de ratón",
            associationItems = listOf(
                AssociationItem("Ratón", R.drawable.placeholder_image, true),
                AssociationItem("Dado", R.drawable.placeholder_image, false),
                AssociationItem("Rosa", R.drawable.placeholder_image, true),
                AssociationItem("Dedo", R.drawable.placeholder_image, false),
                AssociationItem("Rana", R.drawable.placeholder_image, true),
                AssociationItem("Dulce", R.drawable.placeholder_image, false)
            ),
            syllables = listOf("RA", "RE", "RI", "RO", "RU"),
            targetWords = listOf("RARA", "RERE", "RIRI", "RORO")
        )
    )
    
    // Estados del ViewModel
    var currentLetterIndex = mutableStateOf(0)
    var hasCompletedCurrentLetter = mutableStateOf(false)
    var totalStarsEarned = mutableStateOf(0)
    
    // Propiedades computadas
    val currentLetterData: LetterData
        get() = lettersData[currentLetterIndex.value]
    
    val isLastLetter: Boolean
        get() = currentLetterIndex.value >= lettersData.size - 1
    
    val progressPercentage: Float
        get() = (currentLetterIndex.value + 1).toFloat() / lettersData.size.toFloat()
    
    // Métodos públicos
    fun nextLetter(): Boolean {
        return if (!isLastLetter) {
            currentLetterIndex.value++
            hasCompletedCurrentLetter.value = false
            true
        } else {
            false
        }
    }
    
    fun previousLetter(): Boolean {
        return if (currentLetterIndex.value > 0) {
            currentLetterIndex.value--
            hasCompletedCurrentLetter.value = false
            true
        } else {
            false
        }
    }
    
    fun markCurrentLetterCompleted() {
        hasCompletedCurrentLetter.value = true
        totalStarsEarned.value += 3 // 3 estrellas por letra completada
    }
    
    fun resetProgress() {
        currentLetterIndex.value = 0
        hasCompletedCurrentLetter.value = false
        totalStarsEarned.value = 0
    }
    
    fun goToLetter(letterIndex: Int) {
        if (letterIndex in 0 until lettersData.size) {
            currentLetterIndex.value = letterIndex
            hasCompletedCurrentLetter.value = false
        }
    }
    
    fun getAllLetters(): List<String> {
        return lettersData.map { it.letter }
    }
    
    fun getLetterData(letter: String): LetterData? {
        return lettersData.find { it.letter == letter }
    }
}
