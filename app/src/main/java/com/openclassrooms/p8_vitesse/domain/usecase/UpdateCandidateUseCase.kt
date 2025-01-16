package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import javax.inject.Inject

/**
 * Cas d'utilisation pour mettre à jour un candidat dans la base de données.
 * Cette classe encapsule la logique métier associée à la mise à jour des informations d'un candidat.
 * Elle appelle la méthode appropriée dans le repository pour effectuer l'opération.
 *
 * @param repository Le repository utilisé pour effectuer l'opération de mise à jour dans la base de données.
 */
class UpdateCandidateUseCase @Inject constructor(
    private val repository: CandidateRepository
) {
    /**
     * Met à jour un candidat dans la base de données.
     * Cette méthode utilise le repository pour persister les modifications apportées à un candidat existant.
     *
     * @param candidate Le candidat contenant les informations mises à jour à enregistrer.
     * @return Le nombre de lignes affectées par l'opération de mise à jour dans la base de données.
     *         Une valeur positive indique que la mise à jour a été effectuée avec succès.
     */
    suspend fun invoke(candidate: Candidate): Int {
        return repository.updateCandidate(candidate)
    }
}
