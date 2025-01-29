package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * Classe de test pour `UpdateFavoriteStatusUseCase`.
 *
 * Vérifie que la mise à jour du statut favori d'un candidat fonctionne correctement.
 */
@RunWith(MockitoJUnitRunner::class)
class UpdateFavoriteStatusUseCaseTest {

    // Déclaration du UseCase et du repository mocké
    private lateinit var useCase: UpdateFavoriteStatusUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du repository mocké et du UseCase avant chaque test
        candidateRepository = mock(CandidateRepository::class.java)
        useCase = UpdateFavoriteStatusUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que la méthode execute a bien été appelée avec les bons paramètres
    fun `execute should call updateFavoriteStatus with correct parameters`() = runTest {
        // Arrange - Définition des paramètres de mise à jour
        val candidateId = 1L
        val isFavorite = true

        // Simulation du comportement du repository : retourne 1 ligne affectée
        `when`(candidateRepository.updateFavoriteStatus(candidateId, isFavorite)).thenReturn(1)

        // Act - Exécution de la mise à jour
        useCase.execute(candidateId, isFavorite)

        // Assert - Vérification que la méthode updateFavoriteStatus est bien appelée avec les bons paramètres
        verify(candidateRepository).updateFavoriteStatus(candidateId, isFavorite)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que la méthode execute renvoie le nombre de lignes affectées
    fun `execute should return correct number of affected rows`() = runTest {
        // Arrange - Définition des paramètres de mise à jour et du nombre de lignes affectées attendu
        val candidateId = 1L
        val isFavorite = true
        val expectedRowsAffected = 1

        // Simulation du comportement du repository
        whenever(candidateRepository.updateFavoriteStatus(candidateId, isFavorite)).thenReturn(expectedRowsAffected)

        // Act - Exécution de la mise à jour et récupération du nombre de lignes affectées
        val result = useCase.execute(candidateId, isFavorite)

        // Assert - Vérification que le nombre de lignes affectées est correct
        assertEquals(expectedRowsAffected, result)
    }
}