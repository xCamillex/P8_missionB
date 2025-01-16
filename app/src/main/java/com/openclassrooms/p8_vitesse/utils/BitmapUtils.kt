package com.openclassrooms.p8_vitesse.utils

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * Utilitaire pour la gestion des bitmaps.
 * Fournit des méthodes pour créer des bitmaps avec des dimensions spécifiées.
 */
object BitmapUtils {

    /**
     * Crée un bitmap de taille spécifiée et le remplit avec une couleur blanche.
     * Utilisé pour générer un bitmap vierge.
     *
     * @param width La largeur du bitmap à créer.
     * @param height La hauteur du bitmap à créer.
     * @return Un bitmap avec la taille spécifiée, rempli de couleur blanche.
     */
    fun create(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            // Créer un canvas pour dessiner sur le bitmap
            val canvas = Canvas(this)
            // Remplir le canvas avec la couleur blanche
            canvas.drawColor(android.graphics.Color.WHITE)
        }
    }
}
