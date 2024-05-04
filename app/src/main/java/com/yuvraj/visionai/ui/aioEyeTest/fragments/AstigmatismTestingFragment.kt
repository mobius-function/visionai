package com.yuvraj.visionai.ui.aioEyeTest.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.databinding.FragmentHomeProfileBinding
import com.yuvraj.visionai.utils.clients.AlertDialogBox

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AstigmatismTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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