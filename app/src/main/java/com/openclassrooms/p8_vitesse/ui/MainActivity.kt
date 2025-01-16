package com.openclassrooms.p8_vitesse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.ActivityMainBinding
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activité principale de l'application, gère le cycle de vie de l'interface utilisateur.
 * Cette activité initialise la vue avec ViewBinding et charge le fragment d'accueil au démarrage.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Liaison de la vue avec ViewBinding
    private lateinit var binding: ActivityMainBinding

    /**
     * Méthode appelée lors de la création de l'activité.
     * Elle initialise la vue et charge le fragment d'accueil si c'est la première fois que
     * l'activité est créée.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de la vue avec ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Charger HomeScreenFragment au démarrage de l'activité,
        // uniquement si l'activité n'a pas été recréée à partir d'une sauvegarde d'état.
        if (savedInstanceState == null) {
            navigateToHome()
        }
    }

    /**
     * Remplace le conteneur principal par le fragment HomeScreenFragment.
     * Ce fragment est chargé lors du démarrage de l'activité.
     */
    private fun navigateToHome() {
        // Utilisation de la gestion de fragments pour remplacer le fragment actuel
        supportFragmentManager.commit {
            replace(R.id.fragment_container, HomeScreenFragment()) // Remplacer le fragment par HomeScreenFragment
            addToBackStack(null) // Ajouter le fragment à la pile d'arrière-plan pour permettre la navigation arrière
        }
    }
}
