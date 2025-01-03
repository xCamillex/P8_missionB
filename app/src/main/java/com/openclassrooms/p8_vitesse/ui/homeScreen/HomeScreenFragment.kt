package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentHomeScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.addOrEditScreen.AddOrEditScreenFragment
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeScreenViewModel by viewModels()

    // Adapter pour le RecyclerView
    private lateinit var candidateAdapter: CandidateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        setupSearchBar()
        observeViewModel()

        // Charger les candidats initiaux
        viewModel.loadCandidates()

        // Configuration du FAB pour naviguer vers AddEditFragment
        binding.fabAddCandidate.setOnClickListener {
            navigateToAddEditFragment()
        }
    }

    /**
     * Navigue vers le fragment AddEditFragment pour ajouter un candidat.
     */
    private fun navigateToAddEditFragment() {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container, // ID du conteneur défini dans l'activité principale
                AddOrEditScreenFragment()
            )
            .addToBackStack(null) // Permet de revenir en arrière avec le bouton retour
            .commit()
    }

    /**
     * Configure la barre de recherche.
     */
    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filtrer les candidats en fonction du texte saisi
                viewModel.loadCandidates(
                    filter = s?.toString(),
                    favoritesOnly = binding.tabLayout.selectedTabPosition == 1
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Configure le RecyclerView avec l'adapter et la disposition des éléments.
     */
    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter { candidate ->
            onCandidateClicked(candidate)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = candidateAdapter
        }
    }

    /**
     * Configure le TabLayout pour gérer les filtres "Tous" et "Favoris".
     */
    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_all)) // 🇫🇷 Tous
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_favorites))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.loadCandidates(filter = binding.searchEditText.text.toString())
                    1 -> viewModel.loadCandidates(
                        filter = binding.searchEditText.text.toString(),
                        favoritesOnly = true
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * Observe les changements d'état dans le ViewModel.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Loading -> showLoadingState()
                        is HomeUiState.Success -> showCandidates(state.candidates)
                        is HomeUiState.Empty -> showEmptyState()
                        is HomeUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    /**
     * Affiche l'état de chargement.
     */
    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
    }

    /**
     * Affiche les candidats dans le RecyclerView.
     */
    private fun showCandidates(candidates: List<Candidate>) {
        _binding?.let {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
            candidateAdapter.submitList(candidates)
        }
    }

    /**
     * Affiche un message d'état vide si aucun candidat n'est disponible.
     */
    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.VISIBLE
    }

    /**
     * Affiche un message d'erreur.
     */
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Action lors du clic sur un candidat.
     * @param candidate Le candidat sélectionné.
     */
    private fun onCandidateClicked(candidate: Candidate) {
        val fragment = DetailScreenFragment().apply {
            arguments = Bundle().apply {
                putLong("candidate_id", candidate.id ?: -1L)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}