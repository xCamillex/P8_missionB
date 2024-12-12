package com.openclassrooms.p8_vitesse.domain.model.usecase

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import javax.inject.Inject

/**
 * Use Case pour supprimer un candidat spécifique.
 * @param repository Le repository pour gérer les candidats.
 */
class DeleteCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {

    /**
     * Exécute la suppression d'un candidat donné.
     * @param candidate Le candidat à supprimer.
     */
    suspend fun execute(candidate: CandidateDto) {
        repository.deleteCandidate(candidate)
    }
}