package com.openclassrooms.p8_vitesse.ui.detailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel pour gérer les données et la logique de DetailScreen
@HiltViewModel
class DetailScreenViewModel @Inject constructor(val repository: CandidateRepository) : ViewModel() {
    // Supprimer un candidat
    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.deleteCandidate(candidate)
        }
    }
    // Ajouter ou retirer un candidat des favoris
    fun toggleFavorite(candidate: Candidate) {
        viewModelScope.launch {
            val updatedCandidate = candidate.copy(isFavorite = !candidate.isFavorite)
            repository.insertCandidate(updatedCandidate)
        }
    }
}