package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.content.Context
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
 * ViewModel pour gérer l'état de l'écran d'accueil (HomeScreen).
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCandidateUseCase: GetCandidatesUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // État de l'écran d'accueil
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Derniers filtres appliqués
    private var currentFilter: String? = null
    private var showFavoritesOnly: Boolean = false

    /**
     * Charge la liste des candidats selon les filtres actuels.
     * @param filter Le texte à rechercher (nom ou prénom).
     * @param favoritesOnly Si true, filtre uniquement les favoris.
     */
    fun loadCandidates(filter: String? = null, favoritesOnly: Boolean = false) {
        currentFilter = filter
        showFavoritesOnly = favoritesOnly

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            getCandidateUseCase.execute(
                favorite = if (favoritesOnly) true else null,
                name = filter
            ).catch { exception ->
                _uiState.value = HomeUiState.Error(
                    context.getString(R.string.generic_error_message) )
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
     * Utilisé après l’ajout ou la modification d’un candidat.
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
                _uiState.value = HomeUiState.Error(context.getString(R.string.favorite_status_update_error))
            }
        }
    }
}