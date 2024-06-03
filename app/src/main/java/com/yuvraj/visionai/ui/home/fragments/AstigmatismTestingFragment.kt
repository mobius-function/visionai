package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.utils.clients.AlertDialogBox


class AstigmatismTestingFragment : Fragment(R.layout.fragment_home_astigmatism_testing) {
    private var _binding: FragmentHomeAstigmatismTestingBinding? = null
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
        _binding = FragmentHomeAstigmatismTestingBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {
            btnYes.setOnClickListener {
                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Result!",
                    "You have Astigmatism. Please consult a doctor."
                )
            }

            btnNo.setOnClickListener {
                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Result!",
                    "You don't have Astigmatism."
                )
            }
        }
    }
}