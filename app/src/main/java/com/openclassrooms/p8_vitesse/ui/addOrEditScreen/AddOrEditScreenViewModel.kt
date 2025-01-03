package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidateByIdUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.InsertCandidateUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateCandidateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import java.util.regex.Pattern
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
    /**
     * Use case pour insérer un candidat dans la base de données.
     * Utilisé lorsqu'on ajoute un nouveau candidat.
     */
    private val insertCandidateUseCase: InsertCandidateUseCase,

    /**
     * Use case pour récupérer un candidat grâce à son identifiant.
     * Utilisé lorsqu'on est en mode édition pour pré-remplir les champs.
     */
    private val getCandidateByIdUseCase: GetCandidateByIdUseCase,

    /**
     * Use case pour mettre à jour un candidat existant.
     * Utilisé en mode édition, quand l'utilisateur modifie les informations d'un candidat.
     */
    private val updateCandidateUseCase: UpdateCandidateUseCase
) : ViewModel() {
    // État de l'UI : on part d'un état Idle sans erreur, sans chargement, sans données pré-remplies
    private val _uiState = MutableStateFlow<AddOrEditUiState>(AddOrEditUiState.Idle)
    val uiState: StateFlow<AddOrEditUiState> = _uiState

    // Variables pour stocker les données du formulaire
    private var candidateId: Long = -1L
    private var firstName: String = ""
    private var lastName: String = ""
    private var phone: String = ""
    private var email: String = ""
    private var dateOfBirth: Instant? = null
    private var salary: Int? = null
    private var notes: String = ""
    private var photo: Bitmap? = null

    // Méthode pour initialiser l'écran
    // Si on passe un candidateId existant, on charge le candidat et pré-remplit les champs.
    // Sinon, on est en mode ajout.
    fun init(candidateId: Long) {
        if (candidateId <= 0) {
            // Mode Ajout
            this.candidateId = -1
            // On affiche "Ajouter un candidat"
            _uiState.value = AddOrEditUiState.Loaded(
                titleResId = R.string.add_candidate,
                isEditing = false,
                photo = null, // Placeholder par défaut géré par le Fragment
                firstName = "",
                lastName = "",
                phone = "",
                email = "",
                dateOfBirth = null,
                salary = "",
                notes = ""
            )
        } else {
            // Mode Édition
            this.candidateId = candidateId
            loadCandidate(candidateId)
        }
    }

    // Charge un candidat existant pour le mode édition
    private fun loadCandidate(id: Long) {
        _uiState.value = AddOrEditUiState.Loading
        viewModelScope.launch {
            getCandidateByIdUseCase.execute(id).collect { candidate ->
                if (candidate == null) {
                    // Aucune donnée => Erreur
                    _uiState.value = AddOrEditUiState.Error("Candidat introuvable")
                } else {
                    // On remplit nos variables internes
                    firstName = candidate.firstName
                    lastName = candidate.lastName
                    phone = candidate.phoneNumber
                    email = candidate.email
                    dateOfBirth = candidate.dateOfBirth
                    salary = candidate.expectedSalary
                    notes = candidate.note ?: ""
                    photo = candidate.photo
                    // On met à jour l'UI avec les données existantes
                    _uiState.value = AddOrEditUiState.Loaded(
                        titleResId = R.string.edit_candidate,
                        isEditing = true,
                        photo = photo,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone,
                        email = email,
                        dateOfBirth = dateOfBirth,
                        salary = salary?.toString() ?: "",
                        notes = notes
                    )
                }
            }
        }
    }

    // Méthodes pour mettre à jour les champs :

    fun onFirstNameChanged(value: String) {
        firstName = value
    }

    fun onLastNameChanged(value: String) {
        lastName = value
    }

    fun onPhoneChanged(value: String) {
        phone = value
    }

    fun onEmailChanged(value: String) {
        email = value
    }

    fun onDateOfBirthSelected(value: Instant) {
        dateOfBirth = value
    }

    fun onSalaryChanged(value: String) {
        salary = value.toIntOrNull() // Si non convertible, null
    }

    fun onNotesChanged(value: String) {
        notes = value
    }

    fun onPhotoSelected(bitmap: Bitmap) {
        photo = bitmap
    }

    // Méthode déclenchée lorsque l'utilisateur clique sur "Sauvegarder"
    // On vérifie les champs obligatoires et le format de l'email.
    // Si OK, on insère ou on met à jour.
    // Puis on signale le succès pour fermer l'écran.
    fun onSaveClicked() {
        val emptyFields = mutableListOf<AddOrEditUiState.MandatoryField>()

        if (firstName.isBlank()) emptyFields.add(AddOrEditUiState.MandatoryField.FIRST_NAME)
        if (lastName.isBlank()) emptyFields.add(AddOrEditUiState.MandatoryField.LAST_NAME)
        if (phone.isBlank()) emptyFields.add(AddOrEditUiState.MandatoryField.PHONE)
        if (email.isBlank()) emptyFields.add(AddOrEditUiState.MandatoryField.EMAIL)
        if (dateOfBirth == null) emptyFields.add(AddOrEditUiState.MandatoryField.DATE_OF_BIRTH)
        if (salary == null) emptyFields.add(AddOrEditUiState.MandatoryField.EXPECTED_SALARY)
        if (notes.isBlank()) emptyFields.add(AddOrEditUiState.MandatoryField.NOTES)

        if (emptyFields.isNotEmpty()) {
            _uiState.value = AddOrEditUiState.ErrorMandatoryFields(
                "Veuillez remplir tous les champs obligatoires.",
                emptyFields
            )
            return
        }

        // Vérifier le format de l'email
        if (!isEmailValid(email)) {
            _uiState.value = AddOrEditUiState.ErrorEmailFormat("Format d'email invalide.")
            return
        }

        // Tout est bon, on peut sauvegarder
        _uiState.value = AddOrEditUiState.Loading

        val candidateToSave = Candidate(
            id = if (candidateId > 0) candidateId else null,
            firstName = firstName,
            lastName = lastName,
            photo = photo, // S'il est null, on affichera placeholder côté UI
            phoneNumber = phone,
            email = email,
            dateOfBirth = dateOfBirth!!, // Non null car checké
            expectedSalary = salary ?: 0,
            note = notes,
            isFavorite = false
        )

        viewModelScope.launch {
            try {
                if (candidateId > 0) {
                    // Mise à jour
                    updateCandidateUseCase.invoke(candidateToSave)
                    _uiState.value = AddOrEditUiState.Success("Candidat mis à jour avec succès")
                } else {
                    // Ajout
                    insertCandidateUseCase.invoke(candidateToSave)
                    _uiState.value = AddOrEditUiState.Success("Candidat ajouté avec succès")
                }
            } catch (e: Exception) {
                _uiState.value = AddOrEditUiState.Error("Erreur lors de la sauvegarde.")
            }
        }
    }

    // Vérification de l'email via un pattern
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
        )
        return emailPattern.matcher(email).matches()
    }
}