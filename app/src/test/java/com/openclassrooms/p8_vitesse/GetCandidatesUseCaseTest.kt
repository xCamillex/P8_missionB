package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidatesUseCase
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
 * Classe de test pour GetCandidatesUseCase.
 *
 * Vérifie le bon fonctionnement du cas d'utilisation permettant de récupérer une liste de candidats
 * en fonction de différents filtres.
 */
@RunWith(MockitoJUnitRunner::class)
class GetCandidatesUseCaseTest {

    // Déclaration des variables pour le cas d'utilisation et le mock du repository
    private lateinit var getCandidatesUseCase: GetCandidatesUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du mock du repository avant chaque test
        candidateRepository = mock()
        getCandidatesUseCase = GetCandidatesUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérifie que la méthode getCandidates a bien été appelée avec les bons paramètres
    fun `execute should call getCandidates with correct parameters`() = runTest {
        // Arrange - Définition des filtres à appliquer
        val favoriteFilter: Boolean? = true
        val nameFilter: String? = "John"

        // Simulation du comportement du repository qui retourne une liste vide
        whenever(candidateRepository.getCandidates(favoriteFilter, nameFilter))
            .thenReturn(flowOf(emptyList()))

        // Act - Exécution du cas d'utilisation
        getCandidatesUseCase.execute(favoriteFilter, nameFilter).first()

        // Assert - Vérification que la méthode getCandidates a bien été appelée avec les bons paramètres
        verify(candidateRepository).getCandidates(favoriteFilter, nameFilter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que getCandidates renvoie la liste des candidats attendue
    fun `execute should return expected list of candidates`() = runTest {
        // Arrange - Définition d'une liste fictive de candidats
        val expectedCandidates = listOf(
            Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = true),
            Candidate(id = 2L, firstName = "Jane", lastName = "Doe", isFavorite = false)
        )

        // Simulation du repository qui retourne cette liste sans filtre
        whenever(candidateRepository.getCandidates(null, null))
            .thenReturn(flowOf(expectedCandidates))

        // Act - Récupération de la liste des candidats
        val result = getCandidatesUseCase.execute().first()

        // Assert - Vérification que la liste retournée correspond à celle attendue
        assertEquals(expectedCandidates, result)
    }
}