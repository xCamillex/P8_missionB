package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateCandidateUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateCandidateUseCaseTest {

    // Mock du dépôt pour simuler les opérations de mise à jour des candidats
    @Mock lateinit var candidateRepository: CandidateRepository

    // Mock d'un candidat pour effectuer les tests
    @Mock lateinit var candidate: Candidate

    // Instance de la classe à tester : UpdateCandidateUseCase
    private lateinit var updateCandidateUseCase: UpdateCandidateUseCase

    /**
     * Configuration avant chaque test.
     * Initialisation des mocks et création de l'instance de UpdateCandidateUseCase avec le dépôt simulé.
     */
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this) // Initialisation des objets mockés
        updateCandidateUseCase = UpdateCandidateUseCase(candidateRepository) // Instanciation de l'UseCase à tester
    }

    /**
     * Teste la méthode `updateCandidate` pour un scénario de succès.
     * Vérifie si l'update du candidat est effectué correctement et retourne un résultat attendu.
     *
     * - Arrange : Prépare un candidat avec les informations à mettre à jour et simule un retour de
     * succès du dépôt.
     * - Act : Appelle la méthode `execute` de l'UseCase avec les données du candidat.
     * - Assert : Vérifie que le résultat est vrai (mise à jour réussie) et que la méthode `
     * updateCandidate` a été appelée.
     */
    @Test
    fun `test updateCandidate success`() = runBlocking {
        // Arrange
        val candidateToUpdate = Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = false)
        `when`(candidateRepository.updateCandidate(candidateToUpdate)).thenReturn(true) // Simule un succès

        // Act
        val result = updateCandidateUseCase.execute(candidateToUpdate)

        // Assert
        assertTrue(result) // Vérifie que le résultat est vrai (mise à jour réussie)
        verify(candidateRepository).updateCandidate(candidateToUpdate) // Vérifie que la méthode a bien été appelée avec les bons paramètres
    }

    /**
     * Teste la méthode `updateCandidate` pour un scénario d'échec.
     * Vérifie si une exception est correctement levée lorsque la mise à jour échoue.
     *
     * - Arrange : Prépare un candidat avec les informations à mettre à jour et simule une exception
     * d'erreur lors de la mise à jour dans le dépôt.
     * - Act : Appelle la méthode `execute` de l'UseCase et attend une exception.
     * - Assert : Vérifie que l'exception est bien une `RuntimeException` et que le message est correct.
     */
    @Test
    fun `test updateCandidate failure`() = runBlocking {
        // Arrange
        val candidateToUpdate = Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = false)
        `when`(candidateRepository.updateCandidate(candidateToUpdate)).thenThrow(RuntimeException("Database error")) // Simule une erreur de base de données

        // Act
        try {
            updateCandidateUseCase.execute(candidateToUpdate)
            fail("Exception should have been thrown") // Si la méthode ne lance pas l'exception, le test échoue
        } catch (e: Exception) {
            // Assert
            assertTrue(e is RuntimeException) // Vérifie que l'exception est une RuntimeException
            assertEquals("Database error", e.message) // Vérifie que le message de l'exception est correct
        }
    }
}

