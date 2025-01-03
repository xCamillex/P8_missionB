package com.openclassrooms.p8_vitesse.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.ActivityMainBinding
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Initialisation de la vue avec ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Charger HomeScreenFragment au démarrage de l'activité
        if (savedInstanceState == null) {
            navigateToHome()
        }
    }

    /**
     * Remplace le conteneur principal par HomeFragment.
     */
    private fun navigateToHome() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, HomeScreenFragment())
            addToBackStack(null)
        }
    }
}