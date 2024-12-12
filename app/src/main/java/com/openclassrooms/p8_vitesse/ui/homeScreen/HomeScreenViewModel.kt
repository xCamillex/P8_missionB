package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.utils.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de HomeScreen
@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: CandidateRepository) :
    ViewModel() {

    // État du filtre (Tous ou Favoris)
    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter

    /**
     * Liste des candidats filtrés en fonction de l'état du filtre.
     * - `FilterType.ALL` : Tous les candidats.
     * - `FilterType.FAVORITES` : Candidats favoris uniquement.
     */
    val filteredCandidates = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.getAllCandidates()
            FilterType.FAVORITES -> repository.getFavoriteCandidates()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Liste de tous les candidats récupérés depuis le repository.
     */
    private val _allCandidates = MutableStateFlow<List<CandidateDto>>(emptyList())
    val allCandidates: StateFlow<List<CandidateDto>> = _allCandidates

    /**
     * Liste des candidats favoris récupérés depuis le repository.
     */
    private val _favoriteCandidates = MutableStateFlow<List<CandidateDto>>(emptyList())
    val favoriteCandidates: StateFlow<List<CandidateDto>> = _favoriteCandidates

    /**
     * Charge tous les candidats depuis le repository.
     * Les données sont collectées et stockées dans `_allCandidates`.
     */
    fun loadCandidates() {
        viewModelScope.launch {
            repository.getAllCandidates().collect { candidates ->
                _allCandidates.value = candidates
            }
        }
    }

    /**
     * Change le filtre appliqué pour la liste des candidats.
     * @param filterType Le type de filtre à appliquer (tous ou favoris).
     */
    fun setFilter(filterType: FilterType) {
        _filter.value = filterType
    }

    /**
     * Charge les candidats favoris depuis le repository.
     * Les données sont collectées et stockées dans `_favoriteCandidates`.
     */
    fun loadFavoritesCandidates() {
        viewModelScope.launch {
            repository.getFavoriteCandidates().collect { candidates ->
                _favoriteCandidates.value = candidates
            }
        }
    }
}