package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate

/**
 * Adaptateur pour afficher une liste de candidats dans un RecyclerView.
 * Gère les éléments de la liste via un ViewHolder et applique un mécanisme de différenciation pour
 * les mises à jour efficaces.
 */
class CandidateAdapter(
    private val onItemClick: (Candidate) -> Unit // Fonction de clic à appeler lorsqu'un candidat est sélectionné
) : ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(CandidateDiffCallback()) {

    /**
     * ViewHolder pour un élément candidat.
     * Représente un item dans la liste de candidats avec ses vues liées.
     * @param binding Liaison des vues avec le layout XML pour l'élément candidat.
     * @param onItemClick Fonction de clic qui sera appelée lors de la sélection d'un candidat.
     */
    class CandidateViewHolder(
        private val binding: ItemCandidateBinding, // Liaison de la vue pour un élément de candidat
        private val onItemClick: (Candidate) -> Unit // Fonction de clic
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Lie un candidat aux vues de l'item.
         * @param candidate L'objet candidat à afficher dans les vues.
         */
        fun bind(candidate: Candidate) {
            // Définir le nom du candidat
            binding.candidateName.text = "${candidate.firstName} ${candidate.lastName}"

            // Définir la note du candidat (si présente)
            binding.candidateNote.text = candidate.note ?: ""

            // Définir la photo du candidat, avec un avatar par défaut si aucune photo n'est disponible
            val placeholderBitmap: Bitmap = BitmapFactory.decodeResource(
                binding.root.context.resources,
                R.drawable.default_avatar
            )
            binding.candidatePhoto.setImageBitmap(candidate.photo ?: placeholderBitmap)

            // Ajouter l'action de clic sur l'élément (l'élément complet, ici `root`, c'est-à-dire l'élément racine du `ItemCandidateBinding`)
            binding.root.setOnClickListener {
                onItemClick(candidate)
            }
        }
    }

    /**
     * Crée une nouvelle vue de ViewHolder pour chaque item de la liste.
     * @param parent Le parent dans lequel la vue sera insérée.
     * @param viewType Le type de vue (utilisé pour gérer différents types d'éléments, mais non utilisé ici).
     * @return Un nouveau ViewHolder avec le binding correspondant.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemCandidateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CandidateViewHolder(binding, onItemClick)
    }

    /**
     * Lie un élément de la liste avec un ViewHolder.
     * @param holder Le ViewHolder auquel l'élément doit être lié.
     * @param position La position de l'élément dans la liste.
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(getItem(position)) // Lie le candidat à la vue en utilisant la méthode `bind` du ViewHolder
    }
}

/**
 * Callback pour déterminer les différences entre deux candidats.
 * Utilisé par `DiffUtil` pour optimiser les mises à jour dans la liste.
 */
class CandidateDiffCallback : DiffUtil.ItemCallback<Candidate>() {

    /**
     * Vérifie si deux éléments sont le même, basé sur leur identifiant unique (ID).
     * @param oldItem L'ancien élément.
     * @param newItem Le nouvel élément.
     * @return Vrai si les deux éléments sont le même, faux sinon.
     */
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Vérifie si les contenus de deux éléments sont identiques.
     * @param oldItem L'ancien élément.
     * @param newItem Le nouvel élément.
     * @return Vrai si les contenus sont identiques, faux sinon.
     */
    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}