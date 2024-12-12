package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import javax.inject.Inject

/**
 * UseCase pour récupérer tous les candidats.
 *
 * Cette classe encapsule la logique métier pour obtenir la liste
 * complète des candidats depuis le repository.
 *
 * @property repository Le repository gérant les opérations sur les candidats.
 */
class GetAllCandidatesUseCase @Inject constructor(
    private val repository: CandidateRepository
){
    /**
     * Exécute le UseCase pour récupérer tous les candidats.
     *
     * @return Un flux contenant la liste des candidats.
     */
    suspend fun execute() {
        repository.deleteAllCandidates()
    }
}