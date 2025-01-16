package com.openclassrooms.p8_vitesse.data.convert

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * Convertisseur pour la classe `Bitmap` utilisé par Room pour la persistance des images.
 * Cette classe permet de convertir un objet `Bitmap` en un tableau d'octets (`ByteArray`)
 * et inversement, afin de stocker et récupérer des images dans une base de données.
 */
class BitmapConverter {

    companion object {
        // Qualité de compression pour l'image (de 0 à 100).
        private const val COMPRESSION_QUALITY = 100

        // Début de l'offset pour le tableau d'octets.
        private const val START_OFFSET = 0

        // Format de compression de l'image (ici PNG).
        private val FORMAT = Bitmap.CompressFormat.PNG
    }

    /**
     * Convertit un tableau d'octets en un objet `Bitmap`.
     * Cette méthode est utilisée pour lire des images stockées sous forme de `ByteArray`
     * dans la base de données et les convertir en objets `Bitmap` utilisables.
     *
     * @param bytes Le tableau d'octets représentant l'image à convertir en `Bitmap`.
     * @return L'objet `Bitmap` décodé à partir du tableau d'octets.
     */
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, START_OFFSET, bytes.size)
    }

    /**
     * Convertit un objet `Bitmap` en un tableau d'octets (`ByteArray`).
     * Cette méthode est utilisée pour compresser un objet `Bitmap` en un format binaire
     * avant de le stocker dans la base de données.
     *
     * @param img L'objet `Bitmap` à convertir en tableau d'octets.
     * @return Un tableau d'octets représentant l'image compressée.
     */
    @TypeConverter
    fun fromBitmap(img: Bitmap): ByteArray {
        val os = ByteArrayOutputStream()
        // Compression de l'image au format PNG avec la qualité définie.
        img.compress(FORMAT, COMPRESSION_QUALITY, os)
        return os.toByteArray()
    }
}