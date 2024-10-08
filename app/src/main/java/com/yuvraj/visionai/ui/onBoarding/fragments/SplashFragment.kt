package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingSplashBinding
import com.yuvraj.visionai.firebase.Authentication.Companion.getSignedInUser
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants.USER_DETAILS
import com.yuvraj.visionai.utils.Constants.USER_ONBOARDING_COMPLETED
import com.yuvraj.visionai.utils.helpers.Permissions.allPermissionsGranted



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
                checkForSignedInUser()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingSplashBinding.bind(view)
    }

    private fun checkForSignedInUser() {
        val user = getSignedInUser()
        if (user != null) {

            Toast.makeText(requireActivity(), "Signed in as ${user.displayName}", Toast.LENGTH_SHORT).show()

            val sharedPreferences = requireActivity().getSharedPreferences(USER_DETAILS, MODE_PRIVATE)
            val isOnBoardingCompleted = sharedPreferences.getBoolean(USER_ONBOARDING_COMPLETED, false)

            if(!isOnBoardingCompleted){
                findNavController().navigate(R.id.action_splashFragment_to_detailsFragment)
            } else {
                if(requireActivity().allPermissionsGranted()) {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_checkPermissionsFragment)
                }
            }
        } else{
            // No User found. Navigate to Signup Fragment for user registration
            findNavController().navigate(R.id.action_splashFragment_to_signupFragment)
        }
    }
}