package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateCandidateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique du fragment Add/Edit.
 *
 * Cette classe est responsable de :
 * - Insérer un nouveau candidat.
 * - Mettre à jour les informations d'un candidat existant.
 * - Gérer l'état des opérations (succès, échec, etc.).
 */
@HiltViewModel
class AddOrEditScreenViewModel @Inject constructor(
    private val insertCandidateUseCase: InsertCandidateUseCase,
    private val updateCandidateUseCase: UpdateCandidateUseCase,

    ) : ViewModel() {

    // État actuel de l'opération (succès, erreur, chargement, etc.).
    private val _uiState = MutableStateFlow<AddOrEditUiState>(AddOrEditUiState.Idle)
    val uiState: StateFlow<AddOrEditUiState> = _uiState

    /**
     * Insère un nouveau candidat dans la base de données.
     * @param candidate Le candidat à insérer.
     */
    fun insertCandidate(candidate: Candidate) {
        // Vérifier les champs obligatoires
        if (candidate.firstName.isBlank() || candidate.lastName.isBlank() ||
            candidate.phoneNumber.isBlank() || candidate.emailAddress.isBlank() ||
            candidate.dateOfBirth == 0L
        ) {
            _uiState.value = AddOrEditUiState.Error("All fields are mandatory.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AddOrEditUiState.Loading
            try {
                val id = insertCandidateUseCase.invoke(candidate)
                if (id > 0) {
                    _uiState.value = AddOrEditUiState.Success("Candidate added successfully!", id)
                } else {
                    _uiState.value = AddOrEditUiState.Error("Failed to add candidate.")
                }
            } catch (e: Exception) {
                _uiState.value = AddOrEditUiState.Error(e.message ?: "Unexpected error.")
            }
        }
    }
    /**
     * Met à jour les informations d'un candidat existant dans la base de données.
     * @param candidate Les informations mises à jour du candidat.
     */
    fun updateCandidate(candidate: Candidate) {
        // Vérifier les champs obligatoires
        if (candidate.firstName.isBlank() || candidate.lastName.isBlank() ||
            candidate.phoneNumber.isBlank() || candidate.emailAddress.isBlank() ||
            candidate.dateOfBirth == 0L
        ){
            _uiState.value = AddOrEditUiState.Error("All fields are mandatory.")
            return
        }
            viewModelScope.launch {
            _uiState.value = AddOrEditUiState.Loading
            try {
                val rowsAffected = updateCandidateUseCase.invoke(candidate)
                if (rowsAffected > 0) {
                    _uiState.value = AddOrEditUiState.Success("Candidate updated successfully", candidate.id)
                } else {
                    _uiState.value = AddOrEditUiState.Error("Failed to update candidate.")
                }
            } catch (e: Exception) {
                _uiState.value = AddOrEditUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }
}