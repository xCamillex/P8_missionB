package com.openclassrooms.p8_vitesse.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    fun localeDateTimeStringFromInstant(instant: Instant?, locale: Locale = Locale.getDefault()): String? {
        if (instant == null) return null
        val zdt = instant.atZone(ZoneId.systemDefault())

        // Définir le format en fonction de la langue
        val dateFormat = when (locale.language) {
            "fr" -> "dd/MM/yyyy" // Format pour le français
            "en" -> "MM/dd/yyyy" // Format pour l'anglais
            else -> "yyyy-MM-dd" // Format par défaut pour d'autres langues
        }

        val dateFormatter = DateTimeFormatter.ofPattern(dateFormat, locale)
        return zdt.format(dateFormatter)
    }
}
