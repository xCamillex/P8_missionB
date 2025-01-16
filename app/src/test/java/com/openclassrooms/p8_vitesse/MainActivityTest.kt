package com.openclassrooms.p8_vitesse

import androidx.fragment.app.FragmentManager
import com.openclassrooms.p8_vitesse.ui.MainActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

    // Mock de FragmentManager pour simuler la gestion des fragments dans l'activité
    @Mock
    lateinit var fragmentManager: FragmentManager

    // Instance de MainActivity à tester
    private lateinit var activity: MainActivity

    /**
     * Initialisation de l'activité avant chaque test.
     * Le mock du FragmentManager est injecté, et l'activité est espionnée pour vérifier les
     * appels à ses méthodes.
     */
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this) // Initialisation des mocks
        activity = spy(MainActivity())  // Espionner l'activité pour vérifier son comportement
        activity.supportFragmentManager = fragmentManager // Remplacer le FragmentManager réel par le mock
    }

    /**
     * Teste la méthode navigateToHome pour vérifier si la transaction de fragment est lancée.
     *
     * - Act : Appelle la méthode navigateToHome de l'activité pour simuler la navigation vers
     * l'écran d'accueil.
     * - Assert : Vérifie que le FragmentManager a bien lancé une transaction de fragment en
     * appelant beginTransaction().
     */
    @Test
    fun `test navigateToHome`() {
        // Act
        activity.navigateToHome()

        // Assert
        // Vérifie que la méthode beginTransaction a été appelée sur le FragmentManager, ce qui indique
        // que l'activité a tenté de charger le fragment d'accueil
        verify(fragmentManager).beginTransaction()
    }
}
