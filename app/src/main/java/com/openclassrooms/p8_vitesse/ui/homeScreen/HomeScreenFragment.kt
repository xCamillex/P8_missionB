package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
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
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import com.openclassrooms.p8_vitesse.databinding.FragmentHomeScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.MainActivity
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

        // Initialisation du RecyclerView
        setupRecyclerView()

        // Ajouter les onglets au TabLayout
        setupTabLayout()

        setupSearchBar()
        setupFloatingActionButton()

        // Observer les données du ViewModel
        observeViewModel()

        // Charger les données initiales
        viewModel.loadCandidates()
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
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_all)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_favorites)))

        // Écouter les changements d'onglets
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val showFavorites = tab?.position == 1
                viewModel.loadCandidates(favoritesOnly = showFavorites)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * Configure la barre de recherche pour filtrer les candidats.
     */
    private fun setupSearchBar() {
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            viewModel.loadCandidates(filter = query)
            true
        }
    }
    /**
     * Configure le Floating Action Button pour ajouter un candidat.
     */
    private fun setupFloatingActionButton() {
        binding.fabAddCandidate.setOnClickListener {
            // Naviguer vers l'écran d'ajout (AddScreen)
            (requireActivity() as
                    MainActivity).navigateToAddEdit()
        }
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
     * Affiche l'état vide.
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
     */
    private fun onCandidateClicked(candidate: Candidate) {
        Toast.makeText(requireContext(), "Navigate to Detail Screen for ${candidate.firstName}", Toast.LENGTH_SHORT).show()
        // Naviguer vers l'écran de détails
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}