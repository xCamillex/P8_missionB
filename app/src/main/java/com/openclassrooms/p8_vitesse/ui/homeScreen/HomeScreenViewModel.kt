package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de HomeScreen
@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: CandidateRepository): ViewModel() {

    // État du filtre (Tous ou Favoris)
    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter
    // Liste des candidats filtrés
    val filteredCandidates = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.getAllCandidates()
            FilterType.FAVORITES -> repository.getFavoriteCandidates()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // État pour tous les candidats
    private val _allCandidates = MutableStateFlow<List<CandidateDto>>(emptyList())
    val allCandidates: StateFlow<List<CandidateDto>> = _allCandidates

    // État pour les candidats favoris
    private val _favoriteCandidates = MutableStateFlow<List<CandidateDto>>(emptyList())
    val favoriteCandidates: StateFlow<List<CandidateDto>> = _favoriteCandidates

    // Charger les candidats depuis le repository
    fun loadCandidates() {
        viewModelScope.launch {
            repository.getAllCandidates().collect { candidates ->
                _allCandidates.value = candidates
            }
        }
    }

    fun setFilter(filterType: FilterType) {
        _filter.value = filterType
    }

    // Charger les favoris depuis le repository
    fun loadFavoritesCandidates() {
        viewModelScope.launch {
            repository.getFavoriteCandidates().collect { candidates ->
                _favoriteCandidates.value = candidates
            }
        }
    }
}