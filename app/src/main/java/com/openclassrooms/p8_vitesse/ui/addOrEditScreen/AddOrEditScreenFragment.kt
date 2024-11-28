package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.p8_vitesse.R

class AddOrEditScreenFragment : Fragment() {

    companion object {
        fun newInstance() = AddOrEditScreenFragment()
    }

    private val viewModel: AddOrEditScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_or_edit_screen, container, false)
    }
}