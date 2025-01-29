package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.usecase.DeleteAllCandidatesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.times
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Assert.assertThrows
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.lenient

/**
 * Classe de test pour DeleteAllCandidatesUseCase.
 *
 * Cette classe utilise JUnit et Mockito pour tester le comportement du cas d'utilisation
 * de suppression de tous les candidats.
 */

@RunWith(MockitoJUnitRunner::class)
class DeleteAllCandidatesUseCaseTest {

    private lateinit var deleteAllCandidatesUseCase: DeleteAllCandidatesUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du mock du repository
        candidateRepository = mock()
        deleteAllCandidatesUseCase = DeleteAllCandidatesUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que la méthode deleteAllCandidates() est bien appelée sur le repository.
    fun `execute should call deleteAllCandidates on repository`() = runTest {
        // Act
        deleteAllCandidatesUseCase.execute()

        // Assert
        verify(candidateRepository).deleteAllCandidates() // Vérifie que deleteAllCandidates() a été appelé
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification qu'aucune exception n'est levée lorsque la suppression fonctionne.
    fun `execute should not throw exception when repository deletes all candidates`() = runTest {
        // Act & Assert (ne doit pas lever d'exception)
        deleteAllCandidatesUseCase.execute()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Gestion des erreurs lorsque la suppression échoue.
    fun `execute should throw exception when repository fails to delete all candidates`() =
        runTest {
            // Arrange
            lenient().whenever(candidateRepository.deleteAllCandidates())
                .doThrow(RuntimeException("Database error"))

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                runTest { deleteAllCandidatesUseCase.execute() }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que deleteAllCandidates() est appelé une seule fois
    fun `execute should call deleteAllCandidates once`() = runTest {
        // Act
        deleteAllCandidatesUseCase.execute()

        // Assert
        verify(
            candidateRepository,
            times(1)
        ).deleteAllCandidates() // Vérifie que deleteAllCandidates a été appelé une seule fois
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que aucune autre interaction avec le repository n'a lieu.
    fun `execute should only call deleteAllCandidates on repository`() = runTest {
        // Act
        deleteAllCandidatesUseCase.execute()

        // Assert
        verify(candidateRepository).deleteAllCandidates() // Vérifie que seul deleteAllCandidates a été appelé
        verifyNoMoreInteractions(candidateRepository) // Vérifie qu'aucune autre méthode n'a été appelée
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Validation que le repository injecté est bien utilisé
    fun `execute should use injected repository`() = runTest {
        // Act
        deleteAllCandidatesUseCase.execute()

        // Assert
        verify(candidateRepository).deleteAllCandidates() // Vérifie que le repository est bien utilisé
    }
}