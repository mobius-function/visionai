package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingCheckPermissionsBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS_FOR_CAMERA

/**
 * A simple [Fragment] subclass.
 * Use the [CheckPermissionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckPermissionsFragment : Fragment(R.layout.fragment_on_boarding_check_permissions) {
    private var _binding: FragmentOnBoardingCheckPermissionsBinding? = null
    private val binding get() = _binding!!

    var requiredPermission = REQUIRED_PERMISSIONS_FOR_CAMERA

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
                checkForPermission(requiredPermission)
            }
        }
    }

    private fun checkForPermission(permissions: Array<String>) {
        if (allPermissionsGranted()) {
            // continue
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(requireActivity(),
                    "Permissions granted by the user.",
                    Toast.LENGTH_SHORT).show()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()

                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}