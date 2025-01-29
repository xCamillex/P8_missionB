package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.GetAllCandidatesUseCase
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
 * Classe de test pour GetAllCandidatesUseCase.
 *
 * Vérifie le comportement du cas d'utilisation permettant de récupérer tous les candidats.
 */

@RunWith(MockitoJUnitRunner::class)
class GetAllCandidatesUseCaseTest {

    private lateinit var getAllCandidatesUseCase: GetAllCandidatesUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du mock du repository avant chaque test.
        candidateRepository = mock()
        getAllCandidatesUseCase = GetAllCandidatesUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getAllCandidates() a bien été appelé
    fun `execute should call getAllCandidates`() = runTest {
        // Arrange - Création d'une liste fictive de candidats
        val candidates = listOf(
            Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = true),
            Candidate(id = 2L, firstName = "Jane", lastName = "Doe", isFavorite = false)
        )

        // Simulation du comportement du repository pour renvoyer une Flow contenant la liste de candidats
        whenever(candidateRepository.getAllCandidates()).thenReturn(flowOf(candidates))

        // Act - Exécution du cas d'utilisation
        getAllCandidatesUseCase.execute().first()

        // Assert - Vérification que getAllCandidates() a bien été appelé
        verify(candidateRepository).getAllCandidates()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getAllCandidates() renvoie la liste attendue des candidats
    fun `execute should return expected list of candidates`() = runTest {
        // Arrange - Création d'une liste fictive de candidats
        val candidates = listOf(
            Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = true),
            Candidate(id = 2L, firstName = "Jane", lastName = "Doe", isFavorite = false)
        )

        // Simulation du repository pour renvoyer cette liste sous forme de Flow
        whenever(candidateRepository.getAllCandidates()).thenReturn(flowOf(candidates))

        // Act - Récupération du premier élément du Flow retourné
        val result = getAllCandidatesUseCase.execute().first()

        // Assert - Vérification que la liste retournée est correcte
        assertEquals(candidates, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getAllCandidates() renvoie une liste vide en cas de liste vide
    fun `execute should return empty list when no candidates found`() = runTest {
        // Arrange - Simulation d'un repository retournant une liste vide
        whenever(candidateRepository.getAllCandidates()).thenReturn(flowOf(emptyList()))

        // Act - Récupération du premier élément du Flow retourné
        val result = getAllCandidatesUseCase.execute().first()

        // Assert - Vérification que la liste retournée est bien vide
        assertEquals(emptyList<Candidate>(), result)
    }
}