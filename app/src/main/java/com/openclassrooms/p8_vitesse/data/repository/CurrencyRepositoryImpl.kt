package com.openclassrooms.p8_vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8_vitesse.data.remote.CurrencyApiService
import javax.inject.Inject

/**
 * Implémentation du dépôt des taux de conversion de devises pour récupérer les taux de conversion.
 * Cette classe interagit avec l'API `CurrencyApiService` pour obtenir les taux de conversion de devises,
 * notamment le taux de conversion de l'Euro (EUR) vers la Livre Sterling (GBP).
 *
 * @param apiService L'interface `CurrencyApiService` utilisée pour effectuer des appels à l'API
 * pour récupérer les taux de conversion.
 */
class CurrencyRepositoryImpl @Inject constructor(
    private val apiService: CurrencyApiService
) : CurrencyRepository {

    /**
     * Récupère le taux de conversion de l'Euro (EUR) vers la Livre Sterling (GBP).
     * Cette fonction appelle l'API pour obtenir le taux de conversion en euros et extrait la valeur
     * pour la Livre Sterling.
     *
     * @return Le taux de conversion de EUR à GBP. Si une erreur se produit, retourne 0.0.
     */
    override suspend fun getEurToGbpRate(): Double {
        return try {
            // Log pour indiquer que la récupération du taux de conversion commence
            Log.d("CurrencyRepository", "Fetching EUR to GBP rate from API")

            // Appel à l'API pour obtenir les taux de conversion
            val response = apiService.getRate("eur")

            // Extraction du taux pour GBP depuis la réponse de l'API
            val rate = response.rates["gbp"] ?: 0.0

            // Log pour afficher le taux de conversion récupéré
            Log.d("CurrencyRepository", "Conversion rate: $rate")

            // Retour du taux de conversion
            rate
        } catch (e: Exception) {
            // En cas d'erreur, log de l'erreur et retour de 0.0 comme valeur par défaut
            Log.e("CurrencyRepository", "Error fetching conversion rate", e)
            0.0
        }
    }
}
