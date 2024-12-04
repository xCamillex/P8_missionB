package com.openclassrooms.p8_vitesse.data.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

// Représente un candidat dans la base de données

@Entity(tableName = "candidates")
data class CandidateDto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "photo")
    val photo: String?,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "email")
    val emailAddress: String,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: Instant,

    @ColumnInfo(name = "expected_salary")
    val expectedSalary: Int,

    @ColumnInfo(name = "note")
    val informationNote: String?,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)