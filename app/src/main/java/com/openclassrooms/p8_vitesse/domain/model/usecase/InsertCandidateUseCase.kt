package com.openclassrooms.p8_vitesse.domain.model.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import javax.inject.Inject

class InsertCandidateUseCase {/**
 * Use Case pour insérer un candidat dans la base de données.
 * @property repository Le repository utilisé pour insérer les données.
 */
class InsertCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {
    /**
     * Invoquer l'insertion d'un candidat dans la base de données.
     * @param candidate Le candidat à insérer.
     * @return L'identifiant du candidat inséré.
     */
    suspend fun invoke(candidate: Candidate): Long {
        return repository.insertCandidate(candidate)

    }
}
}