package com.openclassrooms.p8_vitesse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO (Data Access Object) pour accéder aux données des candidats dans la base de données.
 * Cette interface contient toutes les opérations CRUD (Create, Read, Update, Delete) nécessaires
 * pour manipuler les informations des candidats dans la base de données locale à l'aide de Room.
 */
@Dao
interface CandidateDao {

    /**
     * Insère un nouveau candidat dans la base de données.
     * Si un candidat avec le même ID existe déjà, il sera remplacé.
     *
     * @param candidate Le candidat à insérer sous forme de DTO.
     * @return L'ID du candidat inséré.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateDto): Long

    /**
     * Insère plusieurs candidats dans la base de données.
     * Si des candidats avec le même ID existent, ils seront remplacés.
     *
     * @param candidates La liste des candidats à insérer.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidates(candidates: List<CandidateDto>)

    /**
     * Récupère tous les candidats dans la base de données, triés par nom de famille, puis par prénom.
     *
     * @return Un flux contenant la liste de tous les candidats sous forme de DTO.
     */
    @Query("SELECT * FROM candidates ORDER BY last_name, first_name")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    /**
     * Récupère les candidats en fonction de critères de filtrage optionnels.
     * Permet de filtrer par statut de favori et/ou par nom.
     *
     * @param favorite Filtrer par favori (true/false) ou null pour ignorer ce filtre.
     * @param name Filtrer par nom (partiel ou complet) ou null pour ignorer ce filtre.
     * @return Un flux contenant la liste des candidats correspondants.
     */
    @Query(
        """
        SELECT * 
        FROM candidates 
        WHERE (:favorite IS NULL OR is_favorite = :favorite) AND 
        (:name IS NULL OR (first_name LIKE '%' || :name|| '%' OR last_name LIKE '%' || :name || '%'))
        """
    )
    fun getCandidates(favorite: Boolean?, name: String?): Flow<List<CandidateDto>>

    /**
     * Met à jour un candidat dans la base de données.
     *
     * @param candidate Le candidat à mettre à jour sous forme de DTO.
     * @return Le nombre de lignes affectées (en général, 1 si la mise à jour a réussi).
     */
    @Update
    suspend fun updateCandidate(candidate: CandidateDto): Int

    /**
     * Supprime un candidat de la base de données.
     *
     * @param candidate Le candidat à supprimer sous forme de DTO.
     */
    @Delete
    suspend fun deleteCandidate(candidate: CandidateDto)

    /**
     * Supprime tous les candidats de la base de données.
     */
    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()

    /**
     * Récupère un candidat par son ID unique.
     *
     * @param id L'identifiant unique du candidat.
     * @return Un flux contenant le candidat correspondant, ou null si non trouvé.
     */
    @Query("SELECT * FROM candidates WHERE id = :id ")
    fun getById(id: Long): Flow<CandidateDto?>

    /**
     * Met à jour le statut de favori d'un candidat en fonction de son ID.
     *
     * @param id L'identifiant unique du candidat.
     * @param favorite Le nouveau statut de favori (true/false).
     * @return Le nombre de lignes affectées (en général, 1 si la mise à jour a réussi).
     */
    @Query("UPDATE candidates SET is_favorite = :favorite WHERE id = :id")
    suspend fun updateCandidate(id: Long, favorite: Boolean): Int
}