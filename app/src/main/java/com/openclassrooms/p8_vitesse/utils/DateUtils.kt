package com.openclassrooms.p8_vitesse.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

/**
 * Utilitaire de manipulation des dates.
 * Fournit des méthodes pour formater les dates selon la locale spécifiée.
 */
object DateUtils {

    /**
     * Convertit un Instant en une chaîne formatée selon la locale spécifiée.
     * Le format de la date change en fonction de la langue de la locale :
     * - Français : "dd/MM/yyyy"
     * - Anglais : "MM/dd/yyyy"
     * - Autres : "yyyy-MM-dd" (format ISO par défaut)
     *
     * @param instant L'instant à convertir (peut être null).
     * @param locale La locale pour définir le format de la date (par défaut : Locale.getDefault()).
     * @return La chaîne formatée de la date ou null si l'instant est null.
     */
    fun localeDateTimeStringFromInstant(
        instant: Instant?,
        locale: Locale = Locale.getDefault()
    ): String? {
        if (instant == null) return null

        // Convertir l'Instant en ZonedDateTime en utilisant le fuseau horaire système par défaut
        val zdt = instant.atZone(ZoneId.of("UTC"))

        // Définir le format de la date en fonction de la langue
        val dateFormat = when (locale.language) {
            "fr" -> "dd/MM/yyyy" // Format pour le français
            "en" -> "MM/dd/yyyy" // Format pour l'anglais
            else -> "yyyy-MM-dd" // Format par défaut pour d'autres langues
        }

        // Créer le DateTimeFormatter avec le format et la locale
        val dateFormatter = DateTimeFormatter.ofPattern(dateFormat, locale)

        // Retourner la date formatée sous forme de chaîne
        return zdt.format(dateFormatter)
    }
}