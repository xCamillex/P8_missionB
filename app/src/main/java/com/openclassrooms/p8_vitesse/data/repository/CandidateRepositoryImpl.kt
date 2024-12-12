package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.data.dao.CandidateDao
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implémentation de CandidateRepository utilisant Room comme source de données.
 */
class CandidateRepositoryImpl @Inject constructor(
    private val candidateDao: CandidateDao
) : CandidateRepository {

    /**
     * Récupérer tous les candidats depuis la base de données.
     * Convertit les DTO en Domain Models.
     */
    override fun getAllCandidates(): Flow<List<Candidate>> {
        return candidateDao.getAllCandidates().map { dtoList ->
            dtoList.map { it.toModel() }
        }
    }

    /**
     * Récupérer les candidats avec des filtres optionnels.
     * Convertit les DTO en Domain Models.
     */
    override fun getCandidates(favorite: Boolean?, name: String?): Flow<List<Candidate>> {
        return candidateDao.getCandidates(favorite, name).map { dtoList ->
            dtoList.map { it.toModel() }
        }
    }

    /**
     * Récupérer un candidat par son ID.
     * Convertit le DTO en Domain Model.
     */
    override fun getById(id: Long): Flow<Candidate> {
        return candidateDao.getById(id).map { it.toModel() }
    }

    /**
     * Ajouter un candidat dans la base de données.
     * Convertit le Domain Model en DTO.
     */
    override suspend fun insertCandidate(candidate: Candidate): Long {
        return candidateDao.insertCandidate(candidate.toDto())
    }

    /**
     * Mettre à jour les informations d'un candidat.
     * Convertit le Domain Model en DTO.
     */
    override suspend fun updateCandidate(candidate: Candidate): Int {
        return candidateDao.updateCandidate(candidate.toDto())
    }

    /**
     * Supprimer un candidat de la base de données.
     * Convertit le Domain Model en DTO.
     */
    override suspend fun deleteCandidate(candidate: Candidate) {
        candidateDao.deleteCandidate(candidate.toDto())
    }

    /**
     * Supprimer tous les candidats de la base de données.
     */
    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates()
    }

    /**
     * Mettre à jour le statut de favori d'un candidat.
     */
    override suspend fun updateFavoriteStatus(id: Long, favorite: Boolean): Int {
        return candidateDao.updateCandidate(id, favorite)
    }
}