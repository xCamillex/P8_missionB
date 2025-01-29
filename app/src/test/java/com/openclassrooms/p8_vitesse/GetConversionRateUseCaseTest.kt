package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.data.repository.CurrencyRepository
import com.openclassrooms.p8_vitesse.domain.usecase.GetConversionRateUseCase
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
 * Classe de test pour GetConversionRateUseCase.
 *
 * Vérifie que le cas d'utilisation permettant d'obtenir le taux de conversion EUR -> GBP
 * fonctionne correctement en appelant les bonnes méthodes du repository.
 */
@RunWith(MockitoJUnitRunner::class)
class GetConversionRateUseCaseTest {

    // Déclaration des objets nécessaires pour le test
    private lateinit var getConversionRateUseCase: GetConversionRateUseCase
    private lateinit var currencyRepository: CurrencyRepository

    @Before
    fun setup() {
        // Initialisation du repository mocké et du UseCase avant chaque test
        currencyRepository = mock()
        getConversionRateUseCase = GetConversionRateUseCase(currencyRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que la méthode getEurToGbpRate a bien été appelée
    fun `execute should call getEurToGbpRate`() = runTest {
        // Arrange - Définition du taux de conversion attendu
        val expectedRate = 0.85

        // Simulation du comportement du repository : retourne le taux défini
        whenever(currencyRepository.getEurToGbpRate()).thenReturn(expectedRate)

        // Act - Exécution du cas d'utilisation
        getConversionRateUseCase.execute()

        // Assert - Vérification que la méthode getEurToGbpRate a bien été appelée
        verify(currencyRepository).getEurToGbpRate()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    //Vérification que la méthode execute renvoie le taux de conversion attendu
    fun `execute should return the correct conversion rate`() = runTest {
        // Arrange - Définition du taux de conversion attendu
        val expectedRate = 0.85

        // Simulation du comportement du repository
        whenever(currencyRepository.getEurToGbpRate()).thenReturn(expectedRate)

        // Act - Récupération du taux de conversion
        val result = getConversionRateUseCase.execute()

        // Assert - Vérification que la valeur retournée est bien celle attendue
        assertEquals(expectedRate, result)
    }
}