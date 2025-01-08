package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidateByIdUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.GetConversionRateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import com.openclassrooms.p8_vitesse.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel pour gérer les données et la logique de l'écran de détail (DetailScreen).
 *
 * Il permet de :
 * - Charger les informations d'un candidat à partir de son ID.
 * - Actualiser le statut "favori" du candidat.
 * - Supprimer le candidat.
 * - Gérer la navigation vers l'écran d'édition et le retour après suppression.
 */
@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val getCandidateByIdUseCase: GetCandidateByIdUseCase,
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase,
    private val deleteCandidateUseCase: DeleteCandidateUseCase,
    private val getConversionRateUseCase: GetConversionRateUseCase
) : ViewModel() {

    // État de l'écran : Loading, Success(candidate), Error(message)
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    // On stocke le candidat courant après l'avoir chargé pour pouvoir modifier son statut favori ou le supprimer
    private var currentCandidate: Candidate? = null

    // Afficher une erreur avec un message passé depuis le Fragment
    fun showError(message: String) {
        _uiState.value = DetailUiState.Error(message)
    }

    /**
     * Charge un candidat par son ID.
     * Si le candidat existe, met à jour l'état avec [DetailUiState.Success].
     * Sinon, en cas d'erreur ou candidat introuvable, [DetailUiState.Error].
     *
     * @param candidateId L'ID du candidat à afficher
     * Charge un candidat par son ID et calcule le salaire en livres.
     */
    fun loadCandidate(candidateId: Long, errorMessage: String) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            getCandidateByIdUseCase.execute(candidateId)
                .catch { exception ->
                    Log.e("DetailViewModel", "Error fetching candidate", exception)
                    _uiState.value = DetailUiState.Error(errorMessage)
                }
                .collectLatest { candidate ->
                    if (candidate == null) {
                        _uiState.value = DetailUiState.Error(errorMessage)
                    } else {
                        currentCandidate = candidate
                        val convertedSalary = try {
                            Log.d("DetailViewModel", "Fetching conversion rate")
                            val rate = getConversionRateUseCase.execute()
                            Log.d("DetailViewModel", "Conversion rate: $rate")
                            val converted = candidate.expectedSalary * rate
                            String.format("soit £ %.2f", converted)
                        } catch (e: Exception) {
                            Log.e("DetailViewModel", "Error converting salary", e)
                            // En cas de problème avec l'API, on met un fallback
                            "soit £ ??"
                        }

                        _uiState.value = DetailUiState.Success(candidate, convertedSalary)
                    }
                }
        }
    }

    /**
     * Basculer le statut de favori du candidat actuel.
     * Si le candidat est favori, il devient non-favori.
     * Si le candidat n'est pas favori, il devient favori.
     */
    fun toggleFavoriteStatus(errorMessage: String) {
        val candidate = currentCandidate ?: return
        viewModelScope.launch {
            try {
                val newStatus = !candidate.isFavorite
                updateFavoriteStatusUseCase.execute(candidate.id!!, newStatus)
                currentCandidate = candidate.copy(isFavorite = newStatus)
                // Recalculer le salaire converti si besoin (pas forcément nécessaire)
                val oldState = _uiState.value
                if (oldState is DetailUiState.Success) {
                    _uiState.value = oldState.copy(candidate = currentCandidate!!)
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(errorMessage)
            }
        }
    }

    /**
     * Supprime le candidat actuel de la base.
     * Après suppression, le Fragment pourra naviguer vers l'écran d'accueil.
     *
     * Cette méthode est appelée après confirmation dans le Fragment.
     */
    fun deleteCurrentCandidate(errorMessage: String) {
        val candidate = currentCandidate ?: return
        viewModelScope.launch {
            try {
                deleteCandidateUseCase.execute(candidate)
                // Le fragment gère le retour en arrière
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(errorMessage)
            }
        }
    }


    /**
     * Calcule l'âge du candidat à partir de sa date de naissance.
     *
     * @param dateOfBirth La date de naissance du candidat (Instant).
     * @return L'âge en années.
     */
    fun calculateAge(dateOfBirth: Instant): Int {
        val birthday = dateOfBirth.atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(birthday, now).toInt()
    }

    /**
     * Formatage de la date de naissance.
     * Ici, nous faisons simple : on affiche en format "dd/MM/yyyy".
     * En pratique, on pourrait adapter selon la locale.
     *
     * @param dateOfBirth La date de naissance (Instant).
     * @return Une chaîne de caractères formattée.
     */
    fun formatDateOfBirth(dateOfBirth: Instant): String {
        val currentLocale = Locale.getDefault()
        return DateUtils.localeDateTimeStringFromInstant(dateOfBirth, currentLocale)
            ?: "Date invalide"
    }


    /**
     * Conversion du salaire en livres.
     * @param salaryInEuros Le salaire en euros.
     * @return La valeur convertie en livres.
     */
    fun convertSalaryToPounds(salaryInEuros: Int) {
        // Appel au use case pour récupérer le taux de conversion
        viewModelScope.launch {
            try {
                // Récupération du taux de conversion en livres
                val rate = getConversionRateUseCase.execute()

                // Conversion du salaire
                val convertedSalary = salaryInEuros * rate

                // Mise à jour de l'UI avec le salaire converti
                // Utilisez le mécanisme pour notifier l'UI si nécessaire
                _uiState.value = when (val currentState = _uiState.value) {
                    is DetailUiState.Success -> {
                        currentState.copy(convertedSalary = String.format("soit £ %.2f", convertedSalary))
                    }
                    else -> currentState
                }
            } catch (e: Exception) {
                // En cas d'erreur, afficher une valeur par défaut et logguer l'erreur
                Log.e("DetailViewModel", "Error converting salary", e)
                _uiState.value = DetailUiState.Error("Erreur lors de la conversion du salaire")
            }
        }
    }
}