package com.yuvraj.visionai.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingSplashBinding
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SplashFragment : Fragment(R.layout.fragment_on_boarding_splash) {
    private var _binding: FragmentOnBoardingSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        clickableViews()
    }

    private fun clickableViews() {
        binding.apply {
            btnStart.setOnClickListener {
                val action = SplashFragmentDirections.actionSplashFragmentToSignupFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingSplashBinding.bind(view)
    }
}