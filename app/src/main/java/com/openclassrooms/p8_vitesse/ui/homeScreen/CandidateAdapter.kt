package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate


/**
 * Adapter pour afficher la liste des candidats dans un RecyclerView.
 *
 * @param onItemClick Fonction déclenchée lors du clic sur un candidat.
 */
class CandidateAdapter(
    private val onItemClick: (Candidate) -> Unit
) : ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(CandidateDiffCallback()) {

    /**
     * ViewHolder pour un élément candidat.
     *
     * @param binding Liaison des vues avec le layout XML.
     * @param onItemClick Fonction de clic pour un candidat.
     */
    class CandidateViewHolder(
        private val binding: ItemCandidateBinding,
        private val onItemClick: (Candidate) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Lie un candidat aux vues.
         *
         * @param candidate Le candidat à afficher.
         */
        fun bind(candidate: Candidate) {
            binding.candidateName.text = "${candidate.firstName} ${candidate.lastName}"
            binding.candidateNote.text = candidate.informationNote ?: ""

            val placeholderDrawable = ContextCompat.getDrawable(binding.root.context, R.drawable.default_avatar)

            Glide.with(binding.root.context)
                .load(candidate.photo)
                .placeholder(placeholderDrawable)
                .error(R.drawable.ic_person)
                .into(binding.candidatePhoto)

            // Clic sur l'élément
            binding.root.setOnClickListener {
                onItemClick(candidate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemCandidateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CandidateViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * Callback pour déterminer les différences entre deux candidats.
 */
class CandidateDiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem == newItem
    }
}