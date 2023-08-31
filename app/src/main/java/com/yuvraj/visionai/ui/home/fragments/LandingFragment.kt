package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
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
        _binding = FragmentHomeLandingBinding.bind(view)

        binding.tvLandingPage.text = initPython()
    }

    private fun clickableViews() {

        binding.apply {
            tvLandingPage.setOnClickListener {
                findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
            }
        }
    }

    private fun initPython() : String {
        Python.start(AndroidPlatform(this.requireContext()))
        val python = Python.getInstance()
        val pythonFile = python.getModule("test")
        return pythonFile.callAttr("test", 5).toString()
    }
}