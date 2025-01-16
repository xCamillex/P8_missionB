package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidatesUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour l'écran d'accueil de l'application, gère les interactions de l'utilisateur
 * avec la liste des candidats et la gestion des favoris.
 *
 * Il gère l'état de l'interface utilisateur via un `StateFlow` et contient la logique pour charger,
 * mettre à jour et recharger la liste des candidats, en appliquant des filtres de recherche et de favoris.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCandidateUseCase: GetCandidatesUseCase, // Cas d'utilisation pour récupérer les candidats
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase, // Cas d'utilisation pour mettre à jour les favoris
    @ApplicationContext private val context: Context // Contexte de l'application pour l'accès aux ressources
) : ViewModel() {

    // État de l'écran d'accueil, gère les différents états de l'UI (chargement, succès, erreur, vide)
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Derniers filtres appliqués : recherche par texte et filtre des favoris
    private var currentFilter: String? = null
    private var showFavoritesOnly: Boolean = false

    /**
     * Charge la liste des candidats selon les filtres actuels.
     * Appelle le cas d'utilisation [getCandidateUseCase] pour récupérer les données et met à jour
     * l'état de l'UI.
     *
     * @param filter Le texte à rechercher dans les noms/prénoms des candidats.
     * @param favoritesOnly Si true, applique un filtre pour ne montrer que les candidats favoris.
     */
    fun loadCandidates(filter: String? = null, favoritesOnly: Boolean = false) {
        // Mise à jour des filtres actuels
        currentFilter = filter
        showFavoritesOnly = favoritesOnly

        // Lancement d'une coroutine pour récupérer les candidats de manière asynchrone
        viewModelScope.launch {
            // Initialisation de l'état de l'UI en "chargement"
            _uiState.value = HomeUiState.Loading

            // Appel du cas d'utilisation pour récupérer les candidats en fonction des filtres
            getCandidateUseCase.execute(
                favorite = if (favoritesOnly) true else null,
                name = filter
            ).catch { exception ->
                // En cas d'erreur, mise à jour de l'état de l'UI avec un message d'erreur
                _uiState.value = HomeUiState.Error(
                    context.getString(R.string.generic_error_message)
                )
            }.collectLatest { candidates ->
                // Mise à jour de l'état de l'UI en fonction des résultats
                _uiState.value = if (candidates.isEmpty()) {
                    HomeUiState.Empty // Aucun candidat trouvé
                } else {
                    HomeUiState.Success(candidates) // Candidats récupérés avec succès
                }
            }
        }
    }

    /**
     * Recharge la liste des candidats en fonction des derniers filtres appliqués.
     * Utilisé après l'ajout ou la modification d'un candidat pour actualiser l'affichage.
     */
    fun reloadCandidates() {
        loadCandidates(currentFilter, showFavoritesOnly)
    }

    /**
     * Met à jour le statut de favori d'un candidat.
     * L'état de "favori" est inversé et la liste des candidats est rechargée pour refléter le changement.
     *
     * @param candidate Le candidat dont le statut de favori doit être mis à jour.
     */
    fun updateFavoriteStatus(candidate: Candidate) {
        // Lancement d'une coroutine pour mettre à jour le statut de favori du candidat
        viewModelScope.launch {
            try {
                // Inversion du statut de favori
                val newStatus = !candidate.isFavorite
                // Appel du cas d'utilisation pour mettre à jour le statut dans la base de données
                updateFavoriteStatusUseCase.execute(candidate.id!!, newStatus)
                // Recharge la liste des candidats pour appliquer le changement
                loadCandidates(currentFilter, showFavoritesOnly)
            } catch (exception: Exception) {
                // En cas d'erreur, mise à jour de l'état de l'UI avec un message d'erreur
                Log.e("HomeScreenViewModel", "Error updating favorite status", exception)
                _uiState.value =
                    HomeUiState.Error(context.getString(R.string.favorite_status_update_error))
            }
        }
    }
}