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

    /**
     * État de chargement des données.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    /**
     * État vide (aucun candidat à afficher).
     */
    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> get() = _isEmpty

    // État du filtre (Tous ou Favoris)
    private val _filter = MutableStateFlow(FilterType.ALL)
    val filter: StateFlow<FilterType> = _filter

    //Liste des candidats filtrés en fonction du filtre actuel.
    val filteredCandidates = _filter.flatMapLatest { filterType ->
        when (filterType) {
            FilterType.ALL -> repository.getAllCandidates()
            FilterType.FAVORITES -> repository.getFavoriteCandidates()
        }.map { entities ->
            _isLoading.value = false
            _isEmpty.value = entities.isEmpty()
            entities.toCandidateList() // Mapper les entités vers les modèles métier
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    //Charge les données des candidats depuis le repository.
    fun loadCandidates() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllCandidates().collect { candidates ->
                _isLoading.value = false
                _isEmpty.value = candidates.isEmpty()
            }
        }
    }

    /**
     * Change le filtre appliqué pour la liste des candidats.
     *
     * @param filterType Le type de filtre à appliquer (tous ou favoris).
     */
    fun setFilter(filterType: FilterType) {
        _filter.value = filterType
    }
}