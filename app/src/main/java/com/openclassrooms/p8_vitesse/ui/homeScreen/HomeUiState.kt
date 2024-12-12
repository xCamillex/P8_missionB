package com.openclassrooms.p8_vitesse.ui.homeScreen

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto

/**
 * Représente les différents états de l'interface utilisateur pour l'écran d'accueil.
 */
sealed class HomeUiState {
    /**
     * État chargé lorsque les données sont en cours de chargement.
     */
    object Loading : HomeUiState()
    /**
     * État chargé lorsque la liste des candidats est vide.
     */
    object Empty : HomeUiState()
    /**
     * État chargé lorsque les candidats sont disponibles.
     * @param candidates Liste des candidats à afficher.
     */
    data class Success(val candidates: List<CandidateDto>) : HomeUiState()
    /**
     * État chargé lorsqu'une erreur survient.
     * @param message Le message d'erreur à afficher.
     */
    data class Error(val message: String) : HomeUiState()
}