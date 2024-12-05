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

    override fun getAllCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getAllCandidates().map { candidates ->
            candidates.map { it.toDto() }
        }
    }
    override fun getFavoriteCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getFavoriteCandidates().map { candidates ->
            candidates.map { it.toDto() }
        }
    }
    override suspend fun insertCandidate(candidate: CandidateDto) {
        candidateDao.insertCandidate(Candidate.fromDto(candidate))
    }
    override suspend fun deleteCandidate(candidate: CandidateDto) {
        candidateDao.deleteCandidate(Candidate.fromDto(candidate))
    }
    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates()
    }
}