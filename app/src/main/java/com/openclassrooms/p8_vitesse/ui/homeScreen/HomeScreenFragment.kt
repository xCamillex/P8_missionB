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

/**
 * Fragment représentant l'écran d'accueil, où la liste des candidats est affichée.
 * Ce fragment gère l'affichage des candidats dans un RecyclerView, la barre de recherche,
 * les filtres par onglets (Tous et Favoris), et les interactions avec le ViewModel.
 */
@AndroidEntryPoint
class HomeScreenFragment : Fragment() {

    // Lien vers la vue avec ViewBinding
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    // Référence au ViewModel
    private val viewModel: HomeScreenViewModel by viewModels()

    // Adaptateur pour le RecyclerView
    private lateinit var candidateAdapter: CandidateAdapter

    /**
     * Inflater le layout de ce fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Méthode appelée après l'inflation de la vue, pour configurer les éléments interactifs.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurer le RecyclerView, la barre de recherche et les onglets
        setupRecyclerView()
        setupTabLayout()
        setupSearchBar()
        observeViewModel()

        // Charger les candidats initiaux
        viewModel.loadCandidates()

        // Configuration du Floating Action Button (FAB) pour naviguer vers l'écran d'ajout/modification
        binding.fabAddCandidate.setOnClickListener {
            navigateToAddEditFragment()
        }
    }

    /**
     * Navigue vers le fragment AddOrEditScreenFragment pour ajouter ou modifier un candidat.
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
     * Configure la barre de recherche pour filtrer les candidats en fonction de l'entrée de l'utilisateur.
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
     * Configure le RecyclerView pour afficher les candidats.
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
     * Configure les onglets de filtrage (Tous et Favoris) dans le TabLayout.
     */
    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_all))
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
     * Observe les changements d'état du ViewModel et met à jour l'UI en conséquence.
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
     * Affiche l'état de chargement lorsque les candidats sont en train d'être récupérés.
     */
    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
    }

    /**
     * Affiche la liste des candidats dans le RecyclerView.
     * @param candidates Liste des candidats à afficher.
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
     * Affiche un état vide si aucune donnée n'est disponible.
     */
    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.VISIBLE
    }

    /**
     * Affiche un message d'erreur si une erreur survient lors du chargement des candidats.
     * @param message Message d'erreur à afficher.
     */
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateText.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Action lorsqu'un candidat est sélectionné dans la liste.
     * Navigue vers un fragment de détails du candidat.
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

    /**
     * Libère la référence au binding lors de la destruction de la vue.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}