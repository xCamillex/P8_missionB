package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Cas d'utilisation pour récupérer un candidat à partir de son identifiant unique.
 * Cette classe encapsule la logique métier liée à la récupération d'un candidat spécifique,
 * en interrogeant le repository pour obtenir les données.
 *
 * @param candidateRepository Le repository utilisé pour récupérer un candidat depuis la base de données.
 */
class GetCandidateByIdUseCase @Inject constructor(
    private val candidateRepository: CandidateRepository
) {
    /**
     * Récupère un candidat en fonction de son identifiant unique.
     * Cette méthode utilise un flux pour fournir le candidat correspondant.
     * Si aucun candidat n'est trouvé pour l'ID donné, le flux émettra une valeur `null`.
     *
     * @param id L'identifiant unique du candidat à récupérer.
     * @return Un flux [Flow] émettant le candidat correspondant à l'identifiant, ou `null` si non trouvé.
     */
    fun execute(id: Long): Flow<Candidate?> {
        return candidateRepository.getById(id)
    }
}
