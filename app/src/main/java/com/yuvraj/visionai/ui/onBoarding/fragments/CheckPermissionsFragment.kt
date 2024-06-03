package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingCheckPermissionsBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS
import com.yuvraj.visionai.utils.helpers.Permissions.allPermissionsGranted


class CheckPermissionsFragment : Fragment(R.layout.fragment_on_boarding_check_permissions) {
    private var _binding: FragmentOnBoardingCheckPermissionsBinding? = null
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
        _binding = FragmentOnBoardingCheckPermissionsBinding.bind(view)

        binding.tvDescription.text = "This app requires camera permission to work. Please grant the permission to continue. \n\n" +
                "- Camera permission is required to capture images of the eye for testing and calculating user's distance from the phone.\n" +
                "Click on the button below to grant the permission."
    }

    private fun clickableViews() {
        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnGrantPermission.setOnClickListener {
                startPermissionsRequest(REQUIRED_PERMISSIONS)
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }

        if (requireActivity().allPermissionsGranted()) {
            // Navigate to MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        } else {
            // continue asking for permissions
        }
    }

    private fun startPermissionsRequest(permissions: Array<String>) {
        requestMultiplePermissions.launch(permissions)
    }
}
