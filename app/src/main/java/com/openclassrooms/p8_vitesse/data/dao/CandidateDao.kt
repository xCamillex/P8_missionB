package com.openclassrooms.p8_vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {

    // Insérer un nouveau candidat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: Candidate): Long

    // Lire tous les candidats
    @Query("SELECT * FROM candidates ORDER BY last_name, first_name")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    // Lire les candidats favoris
    @Query("SELECT * FROM candidates WHERE isFavorite = 1")
    fun getFavoriteCandidates(): Flow<List<CandidateDto>>

    // Mettre à jour un candidat
    @Update
    suspend fun updateCandidate(candidate: CandidateDto): Int

    // Supprimer un candidat
    @Delete
    suspend fun deleteCandidate(candidate: CandidateDto)

    // Supprimer tous les candidats
    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()

    // Lire un candidat par son ID
    @Query("SELECT * FROM candidates WHERE id = :id ")
    fun getById(id: Long): Flow<CandidateDto>

    // Mettre à jour le statut favori d'un candidat
    @Query("UPDATE candidates SET is_favorite = :favorite WHERE id = :id")
    suspend fun updateCandidate(id: Long, favorite: Boolean) : Int
}