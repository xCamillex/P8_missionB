package com.openclassrooms.p8_vitesse.ui.detailScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.p8_vitesse.databinding.FragmentDetailScreenBinding
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurer l'action pour le bouton de retour
        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // TODO: Ajouter l'action pour supprimer un candidat après création du fichier XML
        /*
        binding.deleteButton.setOnClickListener {
            val candidate = // Récupérez le candidat affiché
                viewModel.deleteCandidate(candidate)
        }
        */
        // TODO: Ajouter l'action pour mettre à jour le statut favori après création du fichier XML
        /*
        binding.favoriteButton.setOnClickListener {
            val candidate = // Récupérez le candidat affiché
                viewModel.toggleFavorite(candidate)
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}