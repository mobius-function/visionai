package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingDetailsBinding
import com.yuvraj.visionai.firebase.Authentication
import com.yuvraj.visionai.model.UserPreferences
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants.LATEST_LEFT_EYE_HYPEROPIA
import com.yuvraj.visionai.utils.Constants.LATEST_LEFT_EYE_MYOPIA
import com.yuvraj.visionai.utils.Constants.LATEST_RIGHT_EYE_HYPEROPIA
import com.yuvraj.visionai.utils.Constants.LATEST_RIGHT_EYE_MYOPIA
import com.yuvraj.visionai.utils.Constants.USER_AGE
import com.yuvraj.visionai.utils.Constants.USER_DETAILS
import com.yuvraj.visionai.utils.Constants.USER_ONBOARDING_COMPLETED
import com.yuvraj.visionai.utils.Constants.USER_PHONE
import com.yuvraj.visionai.utils.helpers.Permissions.allPermissionsGranted
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_on_boarding_details) {

    private var _binding: FragmentOnBoardingDetailsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingDetailsBinding.bind(view)

        viewModel.userPreferences.observe(viewLifecycleOwner) { userPreferences ->
            if(userPreferences != null){
                binding.apply {
                    etAge.setText(userPreferences.age.toString())
                    etMobileNumber.setText(userPreferences.phoneNumber)
                    etGender.setText(userPreferences.gender)
                }
            }
        }

        viewModel.loadUserPreferences()
    }


    private fun clickableViews() {
        binding.apply {
            btnCreateAccount.setOnClickListener {

                if(etAge.text.toString().isEmpty()){
                    tilAge.error = "Please enter a valid age"
                    return@setOnClickListener
                } else {
                    tilAge.error = null
                }

                if(etMobileNumber.text.toString().isEmpty()){
                    tilMobileNumber.error = "Please enter your Phone Number"
                    return@setOnClickListener
                } else {
                    tilMobileNumber.error = null
                }


                val user = Authentication.getSignedInUser()
                val userPreferences = UserPreferences(
                    name = user?.displayName!!,
                    phoneNumber = etMobileNumber.text.toString(),
                    age = etAge.text.toString().toInt(),
                    gender = etGender.text.toString(),
                    email = user.email!!
                )

                viewModel.saveUserPreferences(userPreferences)

                val sharedPreferences = requireActivity().getSharedPreferences(USER_DETAILS, MODE_PRIVATE)
                val myEdit = sharedPreferences.edit()

                myEdit.putInt(USER_AGE, binding.etAge.text.toString().toInt())
                myEdit.putLong(USER_PHONE, binding.etMobileNumber.text.toString().toLong())

                //past results
                if(binding.etLastEyePowerLeftMyopia.text.toString().isNotEmpty())
                    myEdit.putFloat(LATEST_LEFT_EYE_MYOPIA, binding.etLastEyePowerLeftMyopia.text.toString().toFloat())
                if(binding.etLastEyePowerRightMyopia.text.toString().isNotEmpty())
                    myEdit.putFloat(LATEST_RIGHT_EYE_MYOPIA, binding.etLastEyePowerRightMyopia.text.toString().toFloat())
                if(binding.etLastEyePowerLeftHyperopia.text.toString().isNotEmpty())
                    myEdit.putFloat(LATEST_LEFT_EYE_HYPEROPIA, binding.etLastEyePowerLeftHyperopia.text.toString().toFloat())
                if(binding.etLastEyePowerRightHyperopia.text.toString().isNotEmpty())
                    myEdit.putFloat(LATEST_RIGHT_EYE_HYPEROPIA, binding.etLastEyePowerRightHyperopia.text.toString().toFloat())

                myEdit.putBoolean(USER_ONBOARDING_COMPLETED, true)
                myEdit.apply()

                if(requireActivity().allPermissionsGranted()){
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    findNavController().navigate(R.id.action_detailsFragment_to_checkPermissionsFragment)
                }
            }

            btnBack.setOnClickListener {
                Authentication.signOutUser()
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
