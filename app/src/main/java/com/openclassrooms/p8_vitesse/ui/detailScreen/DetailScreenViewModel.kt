package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.DeleteCandidateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidateByIdUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.GetConversionRateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateFavoriteStatusUseCase
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

    /**
     * Charge un candidat par son ID.
     * Si le candidat existe, met à jour l'état avec [DetailUiState.Success].
     * Sinon, en cas d'erreur ou candidat introuvable, [DetailUiState.Error].
     *
     * @param candidateId L'ID du candidat à afficher
     * Charge un candidat par son ID et calcule le salaire en livres.
     */
    fun loadCandidate(candidateId: Long) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            getCandidateByIdUseCase.execute(candidateId)
                .catch { exception ->
                    Log.e("DetailViewModel", "Error fetching candidate", exception)
                    _uiState.value = DetailUiState.Error(exception.message ?: "Erreur inconnue")
                }
                .collectLatest { candidate ->
                    if (candidate == null) {
                        _uiState.value = DetailUiState.Error("Candidat introuvable")
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
    fun toggleFavoriteStatus() {
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
                _uiState.value = DetailUiState.Error("Impossible de changer le statut favori")
            }
        }
    }

    /**
     * Supprime le candidat actuel de la base.
     * Après suppression, le Fragment pourra naviguer vers l'écran d'accueil.
     *
     * Cette méthode est appelée après confirmation dans le Fragment.
     */
    fun deleteCurrentCandidate() {
        val candidate = currentCandidate ?: return
        viewModelScope.launch {
            try {
                deleteCandidateUseCase.execute(candidate)
                // Le fragment gère le retour en arrière
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Erreur lors de la suppression")
            }
        }
    }

    /**
     * Calcule l'âge du candidat à partir de sa date de naissance.
     *
     * @param dateOfBirth La date de naissance du candidat (Instant).
     * @return L'âge en années.
     */
    fun calculateAge(dateOfBirth: Long): Int {
        val birthday = Instant.ofEpochMilli(dateOfBirth)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(birthday, now).toInt()
    }

    /**
     * Formatage de la date de naissance.
     * Ici, nous faisons simple : on affiche en format "dd/MM/yyyy".
     * En pratique, on pourrait adapter selon la locale.
     *
     * @param dateOfBirth La date de naissance.
     * @return Une chaîne de caractères formattée.
     */
    fun formatDateOfBirth(dateOfBirth: Long): String {
        val zdt = Instant.ofEpochMilli(dateOfBirth).atZone(ZoneId.systemDefault())
        val day = zdt.dayOfMonth.toString().padStart(2, '0')
        val month = zdt.monthValue.toString().padStart(2, '0')
        val year = zdt.year.toString()
        return "$day/$month/$year"
    }

    /**
     * Conversion du salaire en livres.
     * Pour l'instant, on ne l'implémente pas, juste un TODO.
     *
     * TODO: Intégrer l'API pour convertir les euros en livres.
     * @param salaryInEuros Le salaire en euros.
     * @return La valeur convertie en livres (String formaté).
     */
    fun convertSalaryToPounds(salaryInEuros: Int): String {
        // TODO : Appeler l'API de conversion
        // Pour l'instant, on renvoie une valeur fictive.
        val fakeConversion = salaryInEuros * 0.86 // Juste pour illustrer, sans API réelle
        return String.format("soit £ %.2f", fakeConversion)
    }
}