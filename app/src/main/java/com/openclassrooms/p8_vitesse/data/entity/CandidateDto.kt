package com.openclassrooms.p8_vitesse.data.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import org.threeten.bp.Instant

/**
 * Représente un candidat dans la base de données.
 * Cette classe est utilisée par Room pour la persistance des données.
 * Elle est une version DTO (Data Transfer Object) du modèle de domaine `Candidate`.
 *
 * - @Entity : Spécifie que cette classe est une entité persistée dans la base de données avec le
 * nom de table "candidates".
 * - @PrimaryKey(autoGenerate = true) : L'attribut `id` est la clé primaire et est généré automatiquement
 * par la base de données.
 * - @ColumnInfo(name = "column_name") : Les annotations définissent les noms des colonnes dans la
 * table pour chaque attribut.
 *
 * Les attributs représentent les informations d'un candidat, notamment son prénom, son nom, sa photo,
 * son numéro de téléphone, son email, sa date de naissance, son salaire attendu, sa note et son statut de favori.
 *
 * La méthode `toModel()` est utilisée pour convertir cet objet DTO en un modèle de domaine `Candidate`,
 * afin de le manipuler plus facilement dans la couche métier de l'application.
 */
@Entity(tableName = "candidates")
data class CandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "photo")
    val photo: Bitmap,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Instant,

    @ColumnInfo(name = "expected_salary")
    val expectedSalary: Int,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
) {
    fun toModel(): Candidate {
        return Candidate(
            id = if (this.id == 0L) null else this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            photo = this.photo,
            phoneNumber = this.phoneNumber,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            expectedSalary = this.expectedSalary,
            note = this.note,
            isFavorite = this.isFavorite
        )
    }
}