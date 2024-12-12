package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import javax.inject.Inject

/**
 * Use Case pour supprimer tous les candidats de la base de données.
 * @param repository Le repository pour gérer les candidats.
 */
class DeleteAllCandidatesUseCase @Inject constructor(
    private val repository: CandidateRepository
) {

    /**
     * Exécute la suppression de tous les candidats.
     */
    suspend fun execute() {
        repository.deleteAllCandidates()
    }
}