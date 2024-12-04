package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.p8_vitesse.databinding.FragmentAddOrEditScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditScreenFragment : Fragment() {

    private var _binding: FragmentAddOrEditScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddOrEditScreenViewModel by viewModels()

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

        // TODO: Ajouter l'action du bouton "saveButton" après avoir créé le fichier XML
        /*
        binding.saveButton.setOnClickListener {
            val candidate = // Récupérez les données du formulaire
            viewModel.saveCandidate(candidate)
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}