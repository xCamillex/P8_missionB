package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.DeleteCandidateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doThrow
import org.junit.Assert.assertThrows
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner

/**
 * Classe de test pour DeleteCandidateUseCase.
 *
 * Vérifie le comportement du cas d'utilisation de suppression d'un candidat.
 */

@RunWith(MockitoJUnitRunner::class)
class DeleteCandidateUseCaseTest {

    private lateinit var deleteCandidateUseCase: DeleteCandidateUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du mock du repository avant chaque test.
        candidateRepository = mock()
        deleteCandidateUseCase = DeleteCandidateUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que deleteCandidate a bien été appelé avec le bon candidat
    fun `execute should call deleteCandidate with correct candidate`() = runTest {
        // Arrange - Création d'un candidat fictif
        val candidate = Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = false)

        // Act - Exécution du cas d'utilisation
        deleteCandidateUseCase.execute(candidate)

        // Assert - Vérification que deleteCandidate a bien été appelé avec le bon candidat
        verify(candidateRepository).deleteCandidate(candidate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification qu'aucune exception n'est levée lors de la suppression
    fun `execute should not throw exception when candidate exists`() = runTest {
        // Arrange - Création d'un candidat fictif
        val candidate = Candidate(id = 2L, firstName = "Jane", lastName = "Doe", isFavorite = true)

        // Act & Assert (ne doit pas lever d'exception)
        deleteCandidateUseCase.execute(candidate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    // Vérification qu'une exception est bien levée en cas d'erreur
    fun `execute should throw exception when repository fails`() = runTest {
        // Arrange - Création d'un candidat fictif et simulation d'une erreur du repository
        val candidate = Candidate(id = 3L, firstName = "Alice", lastName = "Smith", isFavorite = false)
        lenient().whenever(candidateRepository.deleteCandidate(candidate)).doThrow(RuntimeException("Database error"))

        // Act & Assert - Vérification qu'une exception est bien levée en cas d'erreur
        assertThrows(RuntimeException::class.java) {
            runTest { deleteCandidateUseCase.execute(candidate) }
        }
    }
}