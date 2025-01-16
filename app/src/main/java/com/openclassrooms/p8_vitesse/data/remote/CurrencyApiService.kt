package com.openclassrooms.p8_vitesse.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface définissant les appels API pour récupérer les taux de conversion des devises.
 * Les appels sont définis avec les annotations de Retrofit pour simplifier l'accès aux ressources de l'API.
 */
interface CurrencyApiService {

    /**
     * Récupère les taux de conversion pour une devise donnée (ex : EUR vers d'autres devises).
     *
     * Cette méthode effectue un appel GET à l'API pour récupérer les taux de conversion de la devise
     * spécifiée par le paramètre `from` (ex : "eur" pour obtenir les taux de conversion par rapport à l'euro).
     *
     * @param from La devise source (par exemple, "eur" pour l'euro).
     * @return Une réponse de type `CurrencyResponse`, qui contient les taux de conversion des devises par rapport à `from`.
     *
     * Exemple d'URL : GET /currencies/eur.json
     * Exemple de réponse :
     * {
     *   "date": "2025-01-16",
     *   "eur": {
     *     "gbp": 0.85,
     *     "usd": 1.12,
     *     "jpy": 130.5
     *   }
     * }
     */
    @GET("currencies/{from}.json")
    suspend fun getRate(
        @Path("from") from: String // Paramètre dynamique pour l'URL (devise source)
    ): CurrencyResponse
}

