package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

/**
 * Représente les différents états de l'interface utilisateur pour le fragment Add/Edit.
 */
sealed class AddOrEditUiState {
    /**
     * État initial ou inactif.
     */
    object Idle : AddOrEditUiState()

    /**
     * État de chargement, utilisé lorsque des opérations sont en cours.
     */
    object Loading : AddOrEditUiState()

    /**
     * État de succès, contient un message informant l'utilisateur.
     *
     * @param message Le message de succès.
     * @param candidateId L'ID du candidat inséré.
     */
    data class Success(val message: String, val candidateId: Long) : AddOrEditUiState()

    /**
     * État d'erreur, contient un message d'erreur.
     *
     * @param error Le message d'erreur.
     */
    data class Error(val error: String) : AddOrEditUiState()
}