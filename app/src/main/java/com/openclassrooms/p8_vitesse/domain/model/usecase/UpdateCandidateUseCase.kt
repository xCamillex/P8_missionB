package com.openclassrooms.p8_vitesse.domain.model.usecase

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import javax.inject.Inject

class UpdateCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {
    /**
     * Invoquer la mise à jour d'un candidat dans la base de données.
     * @param candidate Le candidat à mettre à jour.
     * @return Le nombre de lignes affectées.
     */
    suspend fun invoke(candidate: CandidateDto): Int {
        return repository.updateCandidate(candidate)
    }
}