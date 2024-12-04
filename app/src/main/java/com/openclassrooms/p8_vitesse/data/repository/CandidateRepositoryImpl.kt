package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.data.dao.CandidateDao
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implémentation de CandidateRepository utilisant Room comme source de données.
 */
class CandidateRepositoryImpl @Inject constructor(
    private val candidateDao: CandidateDao
) : CandidateRepository {

    override fun getAllCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getAllCandidates()
    }
    override fun getFavoriteCandidates(): Flow<List<CandidateDto>> {
        return candidateDao.getFavoriteCandidates()
    }
    override suspend fun insertCandidate(candidate: CandidateDto) {
        candidateDao.insertCandidate(candidate)
    }
    override suspend fun deleteCandidate(candidate: CandidateDto) {
        candidateDao.insertCandidate(candidate)
    }
    override suspend fun deleteAllCandidates() {
        candidateDao.deleteAllCandidates()
    }
}