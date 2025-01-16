package com.openclassrooms.p8_vitesse.data.remote

import com.squareup.moshi.Json

/**
 * Représente la réponse obtenue d'une API de conversion de devises, contenant la date de la réponse
 * ainsi qu'un ensemble de taux de conversion pour les différentes devises par rapport à l'euro (EUR).
 *
 * La classe est annotée avec `@Json` de Moshi pour spécifier comment mapper les noms des champs
 * dans la réponse JSON aux propriétés de la classe.
 */
data class CurrencyResponse(
    /**
     * Date de la réponse API sous forme de chaîne de caractères. Cela représente la date à laquelle
     * les taux de change ont été récupérés.
     */
    @Json(name = "date") val date: String,                // Date de la réponse

    /**
     * Carte des taux de conversion pour différentes devises (par exemple, "gbp", "usd").
     * La clé de la carte est le code de la devise (comme "gbp" pour la livre sterling),
     * et la valeur est le taux de conversion par rapport à l'euro (EUR).
     */
    @Json(name = "eur") val rates: Map<String, Double>    // Taux imbriqués sous "eur"
)

