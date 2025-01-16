package com.openclassrooms.p8_vitesse.domain.model

import android.graphics.Bitmap
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import org.threeten.bp.Instant

/**
 * Représente un candidat avec ses informations personnelles.
 * Cette classe sert à encapsuler toutes les données relatives à un candidat,
 * y compris ses informations de contact, ses préférences et sa photo de profil.
 *
 * @param id L'identifiant unique du candidat (facultatif, peut être `null` pour un nouveau candidat).
 * @param firstName Le prénom du candidat.
 * @param lastName Le nom de famille du candidat.
 * @param photo La photo du candidat sous forme de Bitmap (facultatif).
 * @param phoneNumber Le numéro de téléphone du candidat.
 * @param email L'adresse email du candidat.
 * @param dateOfBirth La date de naissance du candidat (par défaut, Instant.EPOCH).
 * @param expectedSalary Le salaire attendu par le candidat.
 * @param note Une note supplémentaire concernant le candidat (facultatif).
 * @param isFavorite Un indicateur de si le candidat est marqué comme favori.
 */
data class Candidate(
    val id: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val photo: Bitmap? = null,
    val phoneNumber: String = "",
    val email: String = "",
    val dateOfBirth: Instant = Instant.EPOCH,
    val expectedSalary: Int = 0,
    val note: String? = null,
    val isFavorite: Boolean = false
) {
    /**
     * Convertit l'objet Candidate en un DTO (Data Transfer Object) pour faciliter le transfert des données.
     * Cela permet de préparer l'objet pour être utilisé dans les couches supérieures
     * (par exemple, l'API ou la vue).
     *
     * @return Le DTO représentant le candidat avec ses informations.
     */
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = this.id ?: 0,  // Si id est null, on attribue 0 par défaut.
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo
                ?: defaultPlaceholderBitmap(),  // Utilise une image par défaut si photo est null.
            phoneNumber = this.phoneNumber,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            expectedSalary = this.expectedSalary,
            note = this.note,
            isFavorite = this.isFavorite
        )
    }

    /**
     * Crée un Bitmap de remplacement par défaut lorsque la photo du candidat est nulle.
     * Ce Bitmap est une image de 1x1 pixel, représentée par un Bitmap transparent.
     *
     * @return Un Bitmap transparent de 1x1 pixel.
     */
    private fun defaultPlaceholderBitmap(): Bitmap {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }
}