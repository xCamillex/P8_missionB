package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.data.dao.CandidateDao
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implémentation concrète du dépôt `CandidateRepository`, responsable de l'accès aux données des candidats
 * via la base de données locale, en utilisant le DAO (Data Access Object) `CandidateDao`.
 * Cette classe implémente les opérations CRUD (Créer, Lire, Mettre à jour, Supprimer) pour les candidats.
 */
class CandidateRepositoryImpl @Inject constructor(
    private val candidateDao: CandidateDao
) : CandidateRepository {

    /**
     * Récupère tous les candidats depuis la base de données.
     * La méthode convertit chaque DTO en un modèle de domaine (Domain Model).
     *
     * @return Un flux contenant la liste des candidats sous forme de modèles de domaine.
     */
    override fun getAllCandidates(): Flow<List<Candidate>> {
        return candidateDao.getAllCandidates().map { dtoList ->
            dtoList.map { it.toModel() } // Conversion de la liste de DTOs en une liste de Domain Models
        }
    }

    /**
     * Récupère les candidats filtrés selon les critères optionnels.
     * La méthode convertit chaque DTO en un modèle de domaine (Domain Model).
     *
     * @param favorite Filtrer par statut de favori (optionnel).
     * @param name Filtrer par nom (optionnel).
     * @return Un flux contenant la liste des candidats filtrés sous forme de modèles de domaine.
     */
    override fun getCandidates(favorite: Boolean?, name: String?): Flow<List<Candidate>> {
        return candidateDao.getCandidates(favorite, name).map { dtoList ->
            dtoList.map { it.toModel() } // Conversion des DTOs filtrés en Domain Models
        }
    }

    /**
     * Récupère un candidat par son ID depuis la base de données.
     * Convertit le DTO en modèle de domaine (Domain Model).
     *
     * @param candidateId L'ID unique du candidat.
     * @return Un flux contenant le candidat correspondant sous forme de modèle de domaine,
     * ou `null` si non trouvé.
     */
    override fun getById(candidateId: Long): Flow<Candidate?> {
        return candidateDao.getById(candidateId).map { candidateDto ->
            candidateDto?.toModel()
        }
    }

    /**
     * Ajoute un candidat dans la base de données.
     * La méthode convertit le modèle de domaine en DTO avant l'insertion.
     *
     * @param candidate Le candidat à insérer.
     * @return L'ID du candidat inséré.
     */
    override suspend fun insertCandidate(candidate: Candidate): Long {
        return candidateDao.insertCandidate(candidate.toDto())
    }

    /**
     * Met à jour les informations d'un candidat dans la base de données.
     * La méthode convertit le modèle de domaine en DTO avant la mise à jour.
     *
     * @param candidate Le candidat à mettre à jour.
     * @return Le nombre de lignes affectées.
     */
    override suspend fun updateCandidate(candidate: Candidate): Int {
        return candidateDao.updateCandidate(candidate.toDto())
    }

    /**
     * Supprime un candidat de la base de données.
     * La méthode convertit le modèle de domaine en DTO avant la suppression.
     *
     * @param candidate Le candidat à supprimer.
     */
    override suspend fun deleteCandidate(candidate: Candidate) {
        candidateDao.deleteCandidate(candidate.toDto())
    }

    /**
     * Supprime tous les candidats de la base de données.
     */
    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates() // Suppression de tous les candidats dans la base de données
    }

    /**
     * Met à jour le statut de favori d'un candidat dans la base de données.
     *
     * @param id L'ID du candidat à mettre à jour.
     * @param favorite Le nouveau statut de favori.
     * @return Le nombre de lignes affectées.
     */
    override suspend fun updateFavoriteStatus(id: Long, favorite: Boolean): Int {
        return candidateDao.updateCandidate(
            id,
            favorite
        ) // Mise à jour du statut de favori du candidat
    }
}