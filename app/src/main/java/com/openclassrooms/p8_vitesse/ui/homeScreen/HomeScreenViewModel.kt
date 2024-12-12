package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.model.usecase.GetCandidatesUseCase
import com.openclassrooms.p8_vitesse.domain.model.usecase.UpdateFavoriteStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de HomeScreen
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCandidateUseCase: GetCandidatesUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
): ViewModel() {

    // État de l'écran d'accueil
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Derniers filtres appliqués
    private var currentFilter: String? = null
    private var showFavoritesOnly: Boolean = false

    // Charge la liste des candidats selon les filtres actuels.
    fun loadCandidates(filter: String? = null, favoritesOnly: Boolean = false) {
        currentFilter = filter
        showFavoritesOnly = favoritesOnly
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getCandidateUseCase.execute(
                favorite = if (favoritesOnly) true else null,
                name = filter
            ).catch { exception ->
                _uiState.value = HomeUiState.Error(exception.message ?: "Unknown error")
            }.collectLatest { candidates ->
                _uiState.value = if (candidates.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(candidates)
                }
            }
        }
    }

    /**
     * Recharge la liste des candidats en fonction des derniers filtres.
     * Utilisé après l'ajout d'un candidat.
     */
    fun reloadCandidates() {
        loadCandidates(currentFilter, showFavoritesOnly)
    }

    /**
     * Met à jour le statut de favori d'un candidat.
     * @param candidate Le candidat dont le statut doit être mis à jour.
     */
    fun updateFavoriteStatus(candidate: Candidate) {
        viewModelScope.launch {
            try {
                val newStatus = !candidate.isFavorite
                updateFavoriteStatusUseCase.execute(candidate.id!!, newStatus)
                // Recharger les candidats après modification
                loadCandidates(currentFilter, showFavoritesOnly)
            } catch (exception: Exception) {
                _uiState.value = HomeUiState.Error("Failed to update favorite status")
            }
        }
    }
}