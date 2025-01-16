package com.openclassrooms.p8_vitesse

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    /**
     * Méthode appelée lors de la création de l'application.
     * Elle initialise les librairies nécessaires au bon fonctionnement de l'application.
     */
    override fun onCreate() {
        super.onCreate()

        /**
         * Initialisation de la bibliothèque AndroidThreeTen pour utiliser les API de gestion du
         * temps de ThreeTenBackport (une implémentation de java.time pour Android).
         * Cette bibliothèque permet d'utiliser des fonctionnalités de manipulation des dates et
         * heures modernes en utilisant Instant, LocalDate, etc.
         */
        AndroidThreeTen.init(this)
    }
}
