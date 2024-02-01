package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingDetailsBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.USER_AGE
import com.yuvraj.visionai.utils.Constants.USER_DETAILS
import com.yuvraj.visionai.utils.Constants.USER_ONBOARDING_COMPLETED
import com.yuvraj.visionai.utils.Constants.USER_PHONE


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

                if(etAge.text.toString().isEmpty()){
                    etAge.error = "Please enter a valid age"
                    return@setOnClickListener
                }

                if(etMobileNumber.text.toString().isEmpty()){
                    etMobileNumber.error = "Please enter your Phone Number"
                    return@setOnClickListener
                }

                val sharedPreferences = requireActivity().getSharedPreferences(USER_DETAILS, MODE_PRIVATE)
                val myEdit = sharedPreferences.edit()

                myEdit.putInt(USER_AGE, binding.etAge.text.toString().toInt())
                myEdit.putLong(USER_PHONE, binding.etMobileNumber.text.toString().toLong())
                myEdit.putBoolean(USER_ONBOARDING_COMPLETED, true)
                myEdit.apply()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingDetailsBinding.bind(view)
//        ConstantsFunctions.setStatusBarTransparent(requireActivity(), false)

    }
}