package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.domain.usecase.InsertCandidateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import junit.framework.TestCase.assertEquals
import org.mockito.junit.MockitoJUnitRunner

/**
 * Classe de test pour `InsertCandidateUseCase`.
 *
 * Vérifie que l'insertion d'un candidat fonctionne correctement et que le repository est bien utilisé.
 */
@RunWith(MockitoJUnitRunner::class)
class InsertCandidateUseCaseTest {

    // Déclaration du UseCase et du repository mocké
    private lateinit var insertCandidateUseCase: InsertCandidateUseCase
    private lateinit var candidateRepository: CandidateRepository

    @Before
    fun setup() {
        // Initialisation du repository mocké et du UseCase avant chaque test
        candidateRepository = mock()
        insertCandidateUseCase = InsertCandidateUseCase(candidateRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que invoke a bien été appelé avec le bon candidat
    fun `invoke should call insertCandidate with correct candidate`() = runTest {
        // Arrange - Création d'un candidat et de l'ID simulé après insertion
        val candidate = Candidate(id = 0L, firstName = "John", lastName = "Doe", isFavorite = false)
        val insertedId = 1L

        // Simulation du comportement du repository : retourne l'ID inséré
        whenever(candidateRepository.insertCandidate(candidate)).thenReturn(insertedId)

        // Act - Exécution de l'insertion
        insertCandidateUseCase.invoke(candidate)

        // Assert - Vérification que la méthode insertCandidate a bien été appelée avec le bon candidat
        verify(candidateRepository).insertCandidate(candidate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que invoke renvoie l'ID du candidat inséré
    fun `invoke should return the inserted candidate ID`() = runTest {
        // Arrange - Création du candidat et de l'ID attendu après insertion
        val candidate = Candidate(id = 0L, firstName = "John", lastName = "Doe", isFavorite = false)
        val expectedId = 1L

        // Simulation du comportement du repository
        whenever(candidateRepository.insertCandidate(candidate)).thenReturn(expectedId)

        // Act - Exécution du UseCase et récupération de l'ID inséré
        val result = insertCandidateUseCase.invoke(candidate)

        // Assert - Vérification que l'ID retourné est bien celui attendu
        assertEquals(expectedId, result)
    }
}