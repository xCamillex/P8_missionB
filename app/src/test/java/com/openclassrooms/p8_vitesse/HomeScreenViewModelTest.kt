package com.openclassrooms.p8_vitesse

import android.content.Context
import com.google.common.base.CharMatcher.any
import com.openclassrooms.p8_vitesse.domain.usecase.GetCandidatesUseCase
import com.openclassrooms.p8_vitesse.domain.usecase.UpdateFavoriteStatusUseCase
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenViewModel
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeUiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeScreenViewModelTest {

    // Mock des dépendances nécessaires
    @Mock lateinit var getCandidateUseCase: GetCandidatesUseCase
    @Mock lateinit var updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
    @Mock
    lateinit var context: Context

    private lateinit var viewModel: HomeScreenViewModel

    /**
     * Initialisation du ViewModel avant chaque test.
     * Les mocks sont initialisés ici, et le ViewModel est instancié avec ces mocks.
     */
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)  // Initialise les mocks
        viewModel = HomeScreenViewModel(getCandidateUseCase, updateFavoriteStatusUseCase, context)
    }

    /**
     * Teste la fonction loadCandidates pour vérifier le bon comportement lorsque les candidats sont
     * récupérés avec succès.
     *
     * - Arrange : Prépare une liste de candidats fictifs et configure le mock de getCandidateUseCase
     * pour retourner cette liste.
     * - Act : Appelle la fonction loadCandidates du ViewModel.
     * - Assert : Vérifie que l'état du ViewModel devient HomeUiState.Success et que les candidats
     * sont correctement transmis.
     */
    @Test
    fun `test loadCandidates success`() = runBlockingTest {
        // Arrange
        val candidates = listOf(Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = true))
        `when`(getCandidateUseCase.execute(any(), any())).thenReturn(candidates)

        // Act
        viewModel.loadCandidates()

        // Assert
        viewModel.uiState.collect {
            assertTrue(it is HomeUiState.Success)
            assertEquals(candidates, (it as HomeUiState.Success).candidates)
        }
    }

    /**
     * Teste la fonction loadCandidates pour vérifier le bon comportement en cas d'erreur.
     *
     * - Arrange : Configure le mock de getCandidateUseCase pour lancer une exception lors de
     * l'exécution de la méthode.
     * - Act : Appelle la fonction loadCandidates du ViewModel.
     * - Assert : Vérifie que l'état du ViewModel devient HomeUiState.Error et que le message
     * d'erreur est correctement transmis.
     */
    @Test
    fun `test loadCandidates error`() = runBlockingTest {
        // Arrange
        val errorMessage = "Error loading candidates"
        `when`(getCandidateUseCase.execute(any(), any())).thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.loadCandidates()

        // Assert
        viewModel.uiState.collect {
            assertTrue(it is HomeUiState.Error)
            assertEquals(errorMessage, (it as HomeUiState.Error).message)
        }
    }
}