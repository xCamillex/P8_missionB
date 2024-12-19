package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.app.DatePickerDialog
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddOrEditScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddOrEditScreenFragment : Fragment() {

    private var _binding: FragmentAddOrEditScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOrEditScreenViewModel by viewModels()

    private var candidatePhoto: Uri? = null

    // Gestionnaire de résultats pour sélectionner une image
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                candidatePhoto = it
                binding.candidatePhoto.setImageURI(it) // Affiche l'image directement depuis l'Uri
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrEditScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopAppBar()
        observeUiState()
        setupSaveButton()
        setupPhotoClick()
        setupDateOfBirthPicker()
    }

    //Configure la TopAppBar avec un titre et une icône de navigation.
    private fun setupTopAppBar() {
        binding.topAppBar.title = getString(R.string.add_candidate)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Configure l'action du clic sur la photo pour ouvrir la galerie.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    private fun setupPhotoClick() {
        binding.candidatePhoto.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    //Observe l'état de l'interface utilisateur via le ViewModel.
    private fun observeUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AddOrEditUiState.Loading -> showLoading()
                        is AddOrEditUiState.Success -> showSuccess(state.message)
                        is AddOrEditUiState.Error -> showError(state.error)
                        AddOrEditUiState.Idle -> Unit // Aucun changement
                    }
                }
            }
        }
    }

    /**
     * Configure l'action du bouton "Save".
     */
    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                val candidate = gatherCandidateData()
                if (candidate != null) {
                    viewModel.insertCandidate(candidate)
                }
            } else {
                Toast.makeText(requireContext(), R.string.missing_fields_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Variable globale pour stocker la date de naissance en millisecondes
    private var dateOfBirth: Long? = null

    // Fonction pour configurer le DatePicker
    private fun setupDateOfBirthPicker() {
        // Configuration du clic pour afficher le DatePicker
        binding.tieAddEditDateOfBirth.setOnClickListener {
            showDatePicker()
        }
    }

    // Fonction pour ouvrir le DatePickerDialog et sélectionner une date
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Si la date de naissance est déjà définie, on la pré-remplit dans le DatePicker
        dateOfBirth?.let { calendar.timeInMillis = it }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Met à jour la date dans dateOfBirth en millisecondes
                calendar.set(year, month, dayOfMonth)
                dateOfBirth = calendar.timeInMillis

                // Affiche la date formatée dans un TextView ou un autre élément de l'UI
                val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(dateOfBirth!!))
                binding.tieAddEditDateOfBirth.setText(dateString)
            },
            calendar.get(Calendar.YEAR), // Année par défaut
            calendar.get(Calendar.MONTH), // Mois par défaut
            calendar.get(Calendar.DAY_OF_MONTH) // Jour par défaut
        ).show()
    }

    /**
     * Rassemble les données saisies dans le formulaire pour créer un objet Candidate.
     * @return Un objet Candidate si toutes les données sont valides, sinon null.
     */
    private fun gatherCandidateData(): Candidate? {
        val firstName = binding.tiFirstname.text.toString().trim()
        val lastName = binding.tiLastname.text.toString().trim()
        val phoneNumber = binding.tiPhone.text.toString().trim()
        val email = binding.tiEmail.text.toString().trim()
        val note = binding.tiNotes.text.toString().trim()
        val salary = binding.tiSalary.text.toString().toIntOrNull()
        val candidatePhoto = candidatePhoto?.toString()

        // Vérifiez que dateOfBirth a une valeur valide
        val dateOfBirth = this.dateOfBirth

        return if (
            firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            phoneNumber.isNotEmpty() &&
            email.isNotEmpty() &&
            salary != null &&
            dateOfBirth != null &&
            candidatePhoto != null
        ) {
            Candidate(
                firstName = firstName,
                lastName = lastName,
                photo = candidatePhoto,
                phoneNumber = phoneNumber,
                emailAddress = email,
                dateOfBirth = dateOfBirth,
                expectedSalary = salary,
                informationNote = note,
                isFavorite = false
            )
        } else {
            null
        }
    }
    /**
     * Vérifie que tous les champs sont remplis et valides.
     * Affiche des erreurs visuelles pour chaque champ non valide.
     *
     * @return true si tous les champs sont valides, false sinon.
     */
    private fun validateFields(): Boolean {
        var isValid = true
        // Prénom
        val firstName = binding.tiFirstname.text.toString().trim()
        if (firstName.isEmpty()) {
            binding.tilFirstname.error = getString(R.string.missing_fields_error)
            isValid = false
        } else {
            binding.tilFirstname.error = null // Supprime l'erreur si corrigé
        }
        // Nom
        val lastName = binding.tiLastname.text.toString().trim()
        if (lastName.isEmpty()) {
            binding.tilLastname.error = getString(R.string.missing_fields_error)
            isValid = false
        } else {
            binding.tilLastname.error = null
        }
        // Téléphone
        val phoneNumber = binding.tiPhone.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            binding.tilPhone.error = getString(R.string.missing_fields_error)
            isValid = false
        } else {
            binding.tilPhone.error = null
        }
        // Email
        val email = binding.tiEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.missing_fields_error)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email_format)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        // Salaire
        val salary = binding.tiSalary.text.toString().trim()
        if (salary.isEmpty()) {
            binding.tilSalary.error = getString(R.string.missing_fields_error)
            isValid = false
        } else {
            binding.tilSalary.error = null
        }
        // Notes
        val note = binding.tiNotes.text.toString().trim()
        if (note.isEmpty()) {
            binding.tilNotes.error = getString(R.string.missing_fields_error)
            isValid = false
        } else {
            binding.tilNotes.error = null
        }
        // Date de naissance
        val dateOfBirth = Instant.now() // Remplacez par la vraie date de naissance saisie par l'utilisateur
        if (dateOfBirth == Instant.EPOCH) {
            Toast.makeText(
                requireContext(),
                getString(R.string.missing_date_of_birth_error),
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        }
        return isValid
    }

    /**
     * Affiche l'état de chargement dans l'interface utilisateur.
     */
    private fun showLoading() {
        // Afficher une ProgressBar ou tout autre indicateur visuel
        Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
    }

    /**
     * Affiche un message de succès.
     * @param message Le message à afficher.
     */
    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    /**
     * Affiche un message d'erreur.
     * @param error Le message d'erreur à afficher.
     */
    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        /**
         * Crée une instance d'AddEditFragment pour éditer un candidat existant.
         * @param candidateId L'ID du candidat à modifier.
         */
        fun newInstance(candidateId: Long): AddOrEditScreenFragment {
            val fragment = AddOrEditScreenFragment()
            val args = Bundle()
            args.putLong(ARG_CANDIDATE_ID, candidateId)
            fragment.arguments = args
            return fragment
        }
    }
}