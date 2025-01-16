package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.homeScreen.CandidateAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CandidateAdapterTest {

    // Mock de la fonction de clic sur un élément de la liste
    @Mock
    lateinit var onItemClick: (Candidate) -> Unit

    private lateinit var adapter: CandidateAdapter

    // Initialisation de l'adaptateur avant chaque test
    @Before
    fun setup() {
        adapter = CandidateAdapter(onItemClick)
    }

    /**
     * Teste que le clic sur un élément de la liste appelle la fonction onItemClick
     * avec le bon candidat.
     *
     * - Arrange : Création d'un candidat à utiliser pour le test et simulation
     *             de l'adaptateur avec une liste contenant ce candidat.
     * - Act : Simule un clic sur un élément en appelant `performClick()` sur
     *         la vue du `ViewHolder`.
     * - Assert : Vérifie que la fonction `onItemClick` a bien été appelée avec
     *            le bon candidat.
     */
    @Test
    fun `test onItemClick`() {
        // Arrange
        val candidate = Candidate(id = 1L, firstName = "John", lastName = "Doe", isFavorite = true)
        val viewHolder = mock(CandidateAdapter.CandidateViewHolder::class.java)

        // Simuler le clic sur l'élément
        adapter.submitList(listOf(candidate))

        // Act
        viewHolder.itemView.performClick()

        // Assert
        verify(onItemClick).invoke(candidate)
    }
}
