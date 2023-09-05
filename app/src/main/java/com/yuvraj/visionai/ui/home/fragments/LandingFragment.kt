package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LandingFragment : Fragment(R.layout.fragment_home_landing) {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

    private var dis = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Initialize Python
        //  Python.start(AndroidPlatform(this.requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeLandingBinding.bind(view)

        binding.apply {
            tvLandingPage.text = "Distance: XX CM"
        }
    }

    private fun clickableViews() {

        binding.apply {
            tvStart.setOnClickListener {
                findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
            }

            tvLandingPage.setOnClickListener {
//                initPython()
            }
        }
    }

//    private fun initPython() {
//        val python = Python.getInstance()
//        val pythonFile = python.getModule("realtime_distance_calculator")
//        pythonFile.callAttr("mainf", binding.tvLandingPage, binding.ivImage)
//    }
}