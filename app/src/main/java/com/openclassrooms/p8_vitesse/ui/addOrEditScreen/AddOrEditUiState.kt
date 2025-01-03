package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.graphics.Bitmap
import org.threeten.bp.Instant

/**
 * Représente les différents états de l'interface utilisateur pour le fragment Add/Edit.
 */
sealed class AddOrEditUiState {
    /**
     * État initial ou inactif.
     */
    object Idle : AddOrEditUiState()

    /**
     * État de chargement, utilisé lorsque des opérations sont en cours.
     */
    object Loading : AddOrEditUiState()

    /**
     * État chargé avec toutes les données nécessaires pour afficher le formulaire.
     *
     * @param titleResId L'ID de la chaîne de caractères pour le titre (par exemple R.string.add_candidate ou R.string.edit_candidate).
     * @param isEditing Indique si on est en mode édition (true) ou ajout (false).
     * @param photo La photo du candidat s'il y en a une, sinon null pour laisser le placeholder.
     * @param firstName Prénom à afficher ou valeur par défaut en mode ajout.
     * @param lastName Nom à afficher ou valeur par défaut en mode ajout.
     * @param phone Téléphone à afficher ou vide.
     * @param email Email à afficher ou vide.
     * @param dateOfBirth Date de naissance ou null si non sélectionnée.
     * @param salary Salaire au format String (peut être vide si pas saisi).
     * @param notes Notes au format String (peut être vide si pas saisi).
     */
    data class Loaded(
        val titleResId: Int,
        val isEditing: Boolean,
        val photo: String?,
        val firstName: String,
        val lastName: String,
        val phone: String,
        val email: String,
        val dateOfBirth: Long?,
        val salary: String,
        val notes: String
    ) : AddOrEditUiState()

    /**
     * État de succès, contient un message informant l'utilisateur.
     * @param message Le message de succès.
     */
    data class Success(val message: String) : AddOrEditUiState()

    /**
     * État d'erreur, contient un message d'erreur.
     * @param error Le message d'erreur.
     */
    data class Error(val error: String) : AddOrEditUiState()

    /**
     * État d'erreur spécifique lorsque des champs obligatoires sont vides.
     * On pourrait distinguer les différents champs plus tard, mais ici on affiche juste un message générique.
     */
    data class ErrorMandatoryFields(val error: String, val emptyFields: List<MandatoryField>) : AddOrEditUiState()

    /**
     * État d'erreur spécifique au format de l'email.
     * @param error Le message d'erreur.
     */
    data class ErrorEmailFormat(val error: String) : AddOrEditUiState()

    enum class MandatoryField {
        FIRST_NAME,
        LAST_NAME,
        PHONE,
        EMAIL,
        DATE_OF_BIRTH
    }
}