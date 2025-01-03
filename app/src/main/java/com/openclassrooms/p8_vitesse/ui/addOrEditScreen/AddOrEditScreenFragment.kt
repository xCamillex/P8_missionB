package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.app.DatePickerDialog
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddOrEditScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class AddOrEditScreenFragment : Fragment() {

    private var _binding: FragmentAddOrEditScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOrEditScreenViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Cette fonction est appelée quand l'utilisateur a sélectionné une image dans la galerie
        if (uri != null) {
            // Convertir l'URI en String (par exemple, en chemin de fichier)
            val photoPath = uri.toString()

            // On informe le ViewModel de la nouvelle photo (en tant que String)
            viewModel.onPhotoSelected(photoPath)

            // Mettre à jour l'affichage de l'image avec l'URI (en tant qu'image via Glide ou autre méthode de chargement d'image)
            Glide.with(requireContext())
                .load(uri)
                .into(binding.candidatePhoto)

            Log.d("AddEditFragment", "Image chargée avec succès.")
        } else {
            Log.e("AddEditFragment", "URI de l'image est null.")
            Toast.makeText(requireContext(), "Impossible de charger l'image", Toast.LENGTH_SHORT).show()
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

        // Récupération de l'ID du candidat s'il existe dans les arguments
        val candidateId = arguments?.getLong("candidate_id", -1L) ?: -1L
        // On informe le ViewModel si c'est un ajout ou une édition
        viewModel.init(candidateId)

        setupToolbar()
        setupPhotoClickListener()
        setupDatePickerClickListener()
        setupSaveButtonClickListener()
        observeViewModel()
    }

    /**
     * Configure la barre d'application (toolbar) avec le bouton de retour.
     */
    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            // On ferme le fragment (revient en arrière)
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Quand on clique sur la photo, on lance la sélection d'image dans la galerie.
     */
    private fun setupPhotoClickListener() {
        binding.candidatePhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    /**
     * Quand on clique sur le champ de date, on ouvre un date picker.
     */
    private fun setupDatePickerClickListener() {
        binding.tieAddEditDateOfBirth.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Quand on clique sur le bouton "Sauvegarder", on envoie les données saisies au ViewModel.
     */
    private fun setupSaveButtonClickListener() {
        binding.saveButton.setOnClickListener {
            // On transmet au ViewModel les dernières valeurs saisies par l'utilisateur
            viewModel.onFirstNameChanged(binding.tiFirstname.text.toString())
            viewModel.onLastNameChanged(binding.tiLastname.text.toString())
            viewModel.onPhoneChanged(binding.tiPhone.text.toString())
            viewModel.onEmailChanged(binding.tiEmail.text.toString())
            viewModel.onSalaryChanged(binding.tiSalary.text.toString())
            viewModel.onNotesChanged(binding.tiNotes.text.toString())

            // On demande au ViewModel de sauvegarder
            viewModel.onSaveClicked()
        }
    }

    /**
     * Observe l'état du ViewModel et met à jour l'UI en conséquence.
     */
    private fun observeViewModel() {
        // On utilise repeatOnLifecycle avec Lifecycle.State.STARTED
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is AddOrEditUiState.Idle -> {
                            // Rien de spécial
                        }
                        is AddOrEditUiState.Loading -> {
                            showLoading(true)
                        }
                        is AddOrEditUiState.Loaded -> {
                            showLoading(false)
                            updateUIWithData(state)
                        }
                        is AddOrEditUiState.Success -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            // Retour à l'écran précédent (liste des candidats)
                            parentFragmentManager.popBackStack()
                        }
                        is AddOrEditUiState.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                        }
                        is AddOrEditUiState.ErrorMandatoryFields -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                            showMandatoryFieldsErrors(state.emptyFields)
                        }
                        is AddOrEditUiState.ErrorEmailFormat -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                            // On peut afficher une erreur sur le champ email
                            binding.tilEmail.error = getString(R.string.missing_fields_error)
                        }
                    }
                }
            }
        }
    }

    /**
     * Met à jour l'interface avec les données chargées (en mode ajout ou édition).
     */
    private fun updateUIWithData(state: AddOrEditUiState.Loaded) {
        binding.topAppBar.setTitle(state.titleResId)

        // Vérifier si l'URI de la photo est disponible et l'afficher
        if (state.photo != null) {
            Glide.with(requireContext())
                .load(state.photo) // Utiliser l'URI de la photo ici
                .into(binding.candidatePhoto)
        } else {
            binding.candidatePhoto.setImageResource(R.drawable.default_avatar)
        }

        binding.tiFirstname.setText(state.firstName)
        binding.tiLastname.setText(state.lastName)
        binding.tiPhone.setText(state.phone)
        binding.tiEmail.setText(state.email)
        binding.tiSalary.setText(state.salary)
        binding.tiNotes.setText(state.notes)

        if (state.dateOfBirth != null) {
            binding.tieAddEditDateOfBirth.setText(formatDate(state.dateOfBirth))
        } else {
            binding.tieAddEditDateOfBirth.setText("")
        }

        // Réinitialiser les erreurs (au cas où)
        clearAllErrors()
    }

    /**
     * Affiche ou cache la ProgressBar et les champs.
     * Si isLoading est vrai, on affiche un chargement.
     * Sinon, on affiche le formulaire.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            binding.saveButton.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.scrollView.visibility = View.VISIBLE
            binding.saveButton.visibility = View.VISIBLE
        }
    }

    /**
     * Affiche une boîte de sélection de date (DatePickerDialog).
     * Quand l'utilisateur choisit une date, on met à jour le ViewModel.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), { _, y, m, d ->
// Mettre à jour le calendrier avec la date sélectionnée
            calendar.set(y, m, d)

            // Convertir en timestamp (Long) et le passer au ViewModel
            val chosenDateInMillis = calendar.timeInMillis
            viewModel.onDateOfBirthSelected(chosenDateInMillis)

            // Mettre à jour le champ avec une version lisible de la date
            binding.tieAddEditDateOfBirth.setText(formatDate(chosenDateInMillis))
        }, year, month, day)

        // Empêche de sélectionner une date future
        dpd.datePicker.maxDate = System.currentTimeMillis()

        dpd.show()
    }

    /**
     * Convertit en texte lisible "jj/mm/aaaa".
     */
    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0') // +1 car JANVIER = 0
        val year = calendar.get(Calendar.YEAR).toString()
        return "$day/$month/$year"
    }

    /**
     * Affiche les erreurs sur les champs obligatoires non remplis.
     */
    private fun showMandatoryFieldsErrors(emptyFields: List<AddOrEditUiState.MandatoryField>) {
        // Réinitialiser toutes les erreurs d'abord
        clearAllErrors()

        // Pour chaque champ vide, afficher une erreur
        emptyFields.forEach { field ->
            when (field) {
                AddOrEditUiState.MandatoryField.FIRST_NAME -> binding.tilFirstname.error = getString(R.string.missing_fields_error)
                AddOrEditUiState.MandatoryField.LAST_NAME -> binding.tilLastname.error = getString(R.string.missing_fields_error)
                AddOrEditUiState.MandatoryField.PHONE -> binding.tilPhone.error = getString(R.string.missing_fields_error)
                AddOrEditUiState.MandatoryField.EMAIL -> binding.tilEmail.error = getString(R.string.missing_fields_error)
                AddOrEditUiState.MandatoryField.DATE_OF_BIRTH -> binding.tilAddEditDateOfBirth.error = getString(R.string.missing_fields_error)
            }
        }
    }

    /**
     * Réinitialise toutes les erreurs.
     */
    private fun clearAllErrors() {
        binding.tilFirstname.error = null
        binding.tilLastname.error = null
        binding.tilPhone.error = null
        binding.tilEmail.error = null
        binding.tilAddEditDateOfBirth.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}