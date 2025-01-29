package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidateByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import org.mockito.junit.MockitoJUnitRunner

/**
 * Classe de test pour GetCandidateByIdUseCase.
 *
 * Vérifie le comportement du cas d'utilisation permettant de récupérer un candidat par son ID.
 */

@RunWith(MockitoJUnitRunner::class)
class GetCandidateByIdUseCaseTest {

    private lateinit var getCandidateByIdUseCase: GetCandidateByIdUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du mock du repository avant chaque test.
        candidateRepository = mock()
        getCandidateByIdUseCase = GetCandidateByIdUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getById a bien été appelé avec le bon ID
    fun `execute should call getById with correct ID`() = runTest {
        // Arrange  - Définition d'un candidat fictif avec un ID spécifique
        val candidateId = 1L
        val expectedCandidate = Candidate(id = candidateId, firstName = "John", lastName = "Doe", isFavorite = true)

        // Simulation du comportement du repository pour renvoyer ce candidat
        whenever(candidateRepository.getById(candidateId)).thenReturn(flowOf(expectedCandidate))

        // Act - Exécution du cas d'utilisation
        getCandidateByIdUseCase.execute(candidateId).first()

        // Assert - Vérification que la méthode getById a bien été appelée avec le bon ID
        verify(candidateRepository).getById(candidateId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getById renvoie le candidat attendu
    fun `execute should return expected candidate when ID is valid`() = runTest {
        // Arrange - Définition d'un candidat fictif valide
        val candidateId = 1L
        val expectedCandidate = Candidate(id = candidateId, firstName = "John", lastName = "Doe", isFavorite = true)

        // Simulation du repository pour renvoyer ce candidat
        whenever(candidateRepository.getById(candidateId)).thenReturn(flowOf(expectedCandidate))

        // Act - Récupération du premier élément du Flow retourné
        val result = getCandidateByIdUseCase.execute(candidateId).first()

        // Assert - Vérification que le candidat retourné est bien celui attendu
        assertEquals(expectedCandidate, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getById renvoie null en cas d'ID invalide
    fun `execute should return null when candidate is not found`() = runTest {
        // Arrange - Utilisation d'un ID qui ne correspond à aucun candidat
        val candidateId = 99L

        // Simulation du repository pour renvoyer null
        whenever(candidateRepository.getById(candidateId)).thenReturn(flowOf(null))

        // Act - Récupération du premier élément du Flow retourné
        val result = getCandidateByIdUseCase.execute(candidateId).first()

        // Assert - Vérification que la valeur retournée est bien null
        assertEquals(null, result)
    }
}