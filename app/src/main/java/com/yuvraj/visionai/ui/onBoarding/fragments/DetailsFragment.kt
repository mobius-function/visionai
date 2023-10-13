package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingDetailsBinding
import com.yuvraj.visionai.ui.home.MainActivity


/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment(R.layout.fragment_on_boarding_details) {

    private var _binding: FragmentOnBoardingDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        clickableViews()
    }


    private fun clickableViews() {
        binding.apply {
            btnCreateAccount.setOnClickListener {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingDetailsBinding.bind(view)
//        ConstantsFunctions.setStatusBarTransparent(requireActivity(), false)

    }
}