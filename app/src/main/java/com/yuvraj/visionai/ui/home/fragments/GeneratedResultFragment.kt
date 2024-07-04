package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.databinding.FragmentHomeGeneratedResultBinding
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.viewModel.UserViewModel

class GeneratedResultFragment : Fragment(R.layout.fragment_home_generated_result) {
    private var _binding: FragmentHomeGeneratedResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeGeneratedResultBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {
            btnDone.setOnClickListener {
                findNavController().navigate(R.id.landingFragment)

                // Disable back press
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}