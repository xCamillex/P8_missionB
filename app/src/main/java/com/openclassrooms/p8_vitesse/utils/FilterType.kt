package com.openclassrooms.p8_vitesse.utils

/**
 * Enumération des types de filtres pour les candidats.
 * Elle permet de spécifier les critères de filtrage des candidats dans l'interface utilisateur.
 *
 * - [ALL] : Représente le filtre "Tous" les candidats, sans restriction.
 * - [FAVORITES] : Représente le filtre "Favoris", qui affiche uniquement les candidats marqués comme favoris.
 */
enum class FilterType {
    ALL,        // Filtre pour afficher tous les candidats
    FAVORITES   // Filtre pour afficher uniquement les candidats favoris
}
