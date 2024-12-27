package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentDetailScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.addOrEditScreen.AddOrEditScreenFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetailScreenFragment : Fragment() {

    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Appelée après la création de la vue.
     * On récupère l'ID du candidat, on charge les données, on met en place la toolbar et on observe le ViewModel.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérer l'ID du candidat depuis les arguments
        val candidateId = arguments?.getLong(ARG_CANDIDATE_ID)
            ?: throw IllegalArgumentException("Candidate ID is required for DetailFragment")

        // Charger les détails du candidat
        viewModel.loadCandidate(candidateId)

        setupTopAppBar()
        observeViewModel()
    }

    /**
     * Configure la TopAppBar avec les icônes et les actions nécessaires.
     */
    private fun setupTopAppBar() {
        val toolbar: MaterialToolbar = binding.topAppBar
        // Action pour la flèche de navigation
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Marquer/Dé-marquer comme favori
        binding.favoriteIcon.setOnClickListener {
            viewModel.toggleFavoriteStatus()
        }

        // Action pour le bouton Modifier
        binding.editIcon.setOnClickListener {
            // Naviguer vers AddEditFragment en mode édition
            val candidate = viewModel.candidate.value
            if (candidate != null && candidate.id != null) {
                val candidateId = candidate.id
                // Naviguer vers AddEditFragment avec candidateId
                val editFragment = AddOrEditScreenFragment.newInstance(candidateId, true)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Candidate not loaded yet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Supprimer le candidat (afficher un dialogue de confirmation)
        binding.deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    /**
     * Affiche une boîte de dialogue demandant à l'utilisateur de confirmer la suppression du candidat.
     *
     */

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(R.string.delete_confirmation) { dialog, _ ->
                dialog.dismiss()
                // L'utilisateur confirme la suppression : on appelle le ViewModel
                viewModel.deleteCandidate()
            }
            .setNegativeButton(R.string.delete_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Observe le ViewModel pour :
     * - Afficher le candidat quand il est disponible.
     * - Réagir aux demandes de navigation (édition, retour après suppression).
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.candidate.collectLatest { candidate ->
                candidate?.let {
                    updateUI(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.navigateToEdit.collectLatest { navigate ->
                if (navigate) {
                    Toast.makeText(requireContext(), "Navigate to Edit Screen", Toast.LENGTH_SHORT)
                        .show()
                    // Implémenter la navigation vers EditFragment ici
                    viewModel.resetNavigationFlags()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.navigateBackAfterDelete.collectLatest { navigate ->
                if (navigate) {
                    // On revient en arrière après la suppression
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    viewModel.resetNavigationFlags()
                }
            }
        }
    }

    /**
     * Met à jour l'interface utilisateur avec les données du candidat.
     * @param candidate Le candidat actuellement sélectionné.
     */
    private fun updateUI(candidate: Candidate) {
        // Mettre à jour le titre avec le nom complet
        binding.topAppBar.title = "${candidate.firstName} ${candidate.lastName.uppercase()}"

        // Mettre à jour l'icône Favori
        binding.favoriteIcon.setImageResource(
            if (candidate.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_empty
        )

        // Mettre à jour la photo du candidat
        val photoToDisplay = candidate.photo
            ?: Uri.parse("android.resource://${requireContext().packageName}/drawable/default_avatar")
        Glide.with(requireContext())
            .load(photoToDisplay)
            .into(binding.profilePhoto)

        // Formate et affiche la date de naissance
        val formattedDate = formatDateOfBirth(candidate.dateOfBirth)
        binding.tvFragmentDetailDateOfbirth.text = formattedDate

        // Calcule et affiche l'âge
        // Modifier calculateAge pour accepter un Long représentant les millisecondes
        fun calculateAge(dateOfBirthMillis: Long): Long {
            val birthDate =
                Instant.ofEpochMilli(dateOfBirthMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val currentDate = LocalDate.now()
            return ChronoUnit.YEARS.between(birthDate, currentDate)
        }

        val age = calculateAge(candidate.dateOfBirth)
        binding.tvFragmentDetailAge.text = getString(R.string.age_format, age)

        val salaryText = "${candidate.expectedSalary} €"
        binding.tvExpectedSalary.text = salaryText

        val notesToDisplay = candidate.informationNote ?: ""
        binding.tvDetailNotesToDisplay.text = notesToDisplay
    }

    /**
     * Formate une date de naissance en chaîne lisible.
     * @param dateOfBirth La date de naissance en Instant.
     * @return La date formatée en String.
     */
    private fun formatDateOfBirth(dateOfBirthMillis: Long): String {
        val dateFormat =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Format de date que vous voulez
        return dateFormat.format(dateOfBirthMillis) ?: ""
    }

    /**
     * Calcule l'âge à partir de la date de naissance.
     * @param dateOfBirth La date de naissance du candidat en Instant.
     * @return L'âge en années.
     */
    private fun calculateAge(dateOfBirth: Instant): Long {
        val birthDate = dateOfBirth.atZone(ZoneId.systemDefault()).toLocalDate()
        val currentDate = LocalDate.now()
        return ChronoUnit.YEARS.between(birthDate, currentDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        /**
         * Crée une instance de DetailFragment avec l'ID du candidat.
         * @param candidateId L'ID du candidat à afficher.
         * @return Une nouvelle instance configurée de DetailFragment.
         */
        fun newInstance(candidateId: Long): DetailScreenFragment {
            return DetailScreenFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, candidateId)
                }
            }
        }
    }
}