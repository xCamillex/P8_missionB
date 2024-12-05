package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentHomeScreenBinding
import com.openclassrooms.p8_vitesse.utils.FilterType
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

// Observer les données du ViewModel
        observeCandidates()

        // Charger les données initiales
        viewModel.loadCandidates()
    }

    /**
     * Configure le RecyclerView avec l'adapter et la disposition des éléments.
     */
    private fun setupRecyclerView() {
        candidateAdapter = CandidateAdapter { candidate ->
            // Action à effectuer lors du clic sur un candidat
            // TODO: Naviguer vers le détail du candidat
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
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.tab_favorites))
        )

        // Écouter les changements d'onglets
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> viewModel.setFilter(FilterType.ALL)
                    1 -> viewModel.setFilter(FilterType.FAVORITES)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * Observe les données des candidats et met à jour l'adapter.
     */
    private fun observeCandidates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.isEmpty.collect { isEmpty ->
                        binding.emptyStateText.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.filteredCandidates.collect { candidates ->
                        binding.recyclerView.visibility =
                            if (candidates.isEmpty()) View.GONE else View.VISIBLE
                        candidateAdapter.submitList(candidates)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}