package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
}