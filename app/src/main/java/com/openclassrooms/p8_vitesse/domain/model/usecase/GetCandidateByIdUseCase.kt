package com.openclassrooms.p8_vitesse.domain.model.usecase

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
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
    fun execute(id: Long): Flow<CandidateDto> {
        return candidateRepository.getById(id)
    }
}