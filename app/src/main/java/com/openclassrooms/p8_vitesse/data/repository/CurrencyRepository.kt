package com.openclassrooms.p8_vitesse.data.repository

/**
 * Interface représentant un dépôt pour récupérer les taux de conversion de devises.
 * Cette interface fournit les méthodes nécessaires pour interagir avec un service de conversion de devises.
 * Les implémentations concrètes de cette interface se chargeront d'effectuer les appels réels à l'API
 * ou à une source de données pour obtenir les taux de conversion.
 */
interface CurrencyRepository {

    /**
     * Récupère le taux de conversion de l'Euro (EUR) vers la Livre Sterling (GBP).
     * Cette méthode est suspendue car elle effectue un appel réseau pour obtenir les données.
     *
     * @return Le taux de conversion entre l'Euro et la Livre Sterling sous forme de `Double`.
     *         Si la récupération échoue ou que la réponse est invalide, la méthode pourrait
     *         retourner une valeur par défaut (par exemple, `0.0`).
     */
    suspend fun getEurToGbpRate(): Double
}
