package com.openclassrooms.p8_vitesse

import com.openclassrooms.p8_vitesse.utils.DateUtils
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Instant
import java.util.Locale

@RunWith(MockitoJUnitRunner::class)
class DateUtilsTest {

    /**
     * Teste la fonction localeDateTimeStringFromInstant pour un locale français.
     *
     * - Arrange : Prépare un instant avec une date spécifique (2025-01-16T10:00:00Z)
     *             et définit le format attendu pour le français.
     * - Act : Appelle la méthode localeDateTimeStringFromInstant en lui passant
     *         l'instant et le locale français.
     * - Assert : Vérifie que la date formatée correspond à la valeur attendue
     *            pour le format français (jj/MM/yyyy).
     */
    @Test
    fun `test localeDateTimeStringFromInstant for French locale`() {
        // Arrange
        val instant = Instant.parse("2025-01-16T10:00:00Z")
        val expectedDate = "16/01/2025"

        // Act
        val formattedDate = DateUtils.localeDateTimeStringFromInstant(instant, Locale.FRENCH)

        // Assert
        assertEquals(expectedDate, formattedDate)
    }

    /**
     * Teste la fonction localeDateTimeStringFromInstant pour un locale anglais.
     *
     * - Arrange : Prépare un instant avec une date spécifique (2025-01-16T10:00:00Z)
     *             et définit le format attendu pour l'anglais.
     * - Act : Appelle la méthode localeDateTimeStringFromInstant en lui passant
     *         l'instant et le locale anglais.
     * - Assert : Vérifie que la date formatée correspond à la valeur attendue
     *            pour le format anglais (MM/dd/yyyy).
     */
    @Test
    fun `test localeDateTimeStringFromInstant for English locale`() {
        // Arrange
        val instant = Instant.parse("2025-01-16T10:00:00Z")
        val expectedDate = "01/16/2025"

        // Act
        val formattedDate = DateUtils.localeDateTimeStringFromInstant(instant, Locale.ENGLISH)

        // Assert
        assertEquals(expectedDate, formattedDate)
    }
}
