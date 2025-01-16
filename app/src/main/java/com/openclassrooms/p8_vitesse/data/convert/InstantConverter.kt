package com.openclassrooms.p8_vitesse.data.convert

import androidx.room.TypeConverter
import org.threeten.bp.Instant

/**
 * Convertisseur pour la classe `Instant` utilisé par Room pour la persistance de données.
 * Ce convertisseur permet de convertir un objet `Instant` en un `Long` (timestamp en millisecondes)
 * et inversement, afin de le stocker dans une base de données.
 */
class InstantConverter {

    /**
     * Convertit un `Long` (représentation en millisecondes) en un `Instant`.
     * Cette méthode est utilisée lors de la lecture des données de la base de données.
     *
     * @param value La valeur de type `Long` à convertir en `Instant`.
     * @return Un objet `Instant` correspondant à la valeur de `Long`, ou `null` si `value` est null.
     */
    @TypeConverter
    fun fromLongNullable(value: Long?): Instant? {
        return value?.let { fromLong(it) }
    }

    /**
     * Convertit un `Instant` en un `Long` (représentation en millisecondes).
     * Cette méthode est utilisée lors de l'écriture des données dans la base de données.
     *
     * @param instant L'objet `Instant` à convertir en `Long`.
     * @return La valeur de type `Long` correspondant au timestamp de `Instant`, ou `null` si
     * `instant` est null.
     */
    @TypeConverter
    fun toLongNullable(instant: Instant?): Long? {
        return instant?.let { toLong(it) }
    }

    /**
     * Convertit un `Long` en un objet `Instant`.
     *
     * @param value La valeur de type `Long` à convertir en `Instant`.
     * @return Un objet `Instant` représentant le moment en temps du `Long`.
     */
    @TypeConverter
    fun fromLong(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    /**
     * Convertit un objet `Instant` en un `Long` (représentation en millisecondes).
     *
     * @param instant L'objet `Instant` à convertir en `Long`.
     * @return La valeur de type `Long` représentant le moment en temps de `Instant`.
     */
    @TypeConverter
    fun toLong(instant: Instant): Long {
        return instant.toEpochMilli()
    }
}