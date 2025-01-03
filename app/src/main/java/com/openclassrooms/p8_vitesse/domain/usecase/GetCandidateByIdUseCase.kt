package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCandidateByIdUseCase @Inject constructor(
    private val candidateRepository: CandidateRepository
) {
    /**
     * Récupère un candidat par son identifiant unique.
     * @param id L'identifiant unique du candidat.
     * @return Un flux contenant le candidat correspondant.
     */
    fun execute(id: Long): Flow<Candidate?> {
        return candidateRepository.getById(id)
    }
}