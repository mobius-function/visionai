package com.yuvraj.visionai.ui.home.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.EyeTestsList
import com.yuvraj.visionai.data.EyeTestMenuList.getEyeTestMenuList
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding
import com.yuvraj.visionai.firebase.Authentication.Companion.signOutUser
import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.ui.onBoarding.MainActivity
import com.yuvraj.visionai.utils.Constants.ASTIGMATISM
import com.yuvraj.visionai.utils.Constants.DRY_EYE
import com.yuvraj.visionai.utils.Constants.HYPEROPIA
import com.yuvraj.visionai.utils.Constants.LOGOUT
import com.yuvraj.visionai.utils.Constants.ML_MODEL
import com.yuvraj.visionai.utils.Constants.MYOPIA

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
        initViews(view)
        checkForPermission()
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeLandingBinding.bind(view)

        val tests = getEyeTestMenuList()

        val adapter = EyeTestsList(tests, this::onListItemClick)
        binding.testsRecyclerView.adapter = adapter
        binding.testsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun clickableViews() {

        binding.apply {
//            btnLogOut.setOnClickListener {
//                signOutUser()
//
//                val intent = Intent(requireActivity(), MainActivity::class.java)
//                startActivity(intent)
//
//                requireActivity().finish()
//            }
        }
    }

    private fun onListItemClick(test: EyeTests) {
        when (test.id) {
            MYOPIA -> {
                findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
            }
            HYPEROPIA -> {
                findNavController().navigate(R.id.action_homeLandingFragment_to_hyperopiaTestingFragment)
            }
            ASTIGMATISM -> {
                findNavController().navigate(R.id.action_homeLandingFragment_to_astigmatismTestingFragment)
            }
            DRY_EYE -> {
                findNavController().navigate(R.id.action_landingFragment_to_dryEyeTestingFragment)
            }
            ML_MODEL -> {
                findNavController().navigate(R.id.action_landingFragment_to_testingFragment)
            }
            LOGOUT -> {
                 signOutUser()
                 val intent = Intent(requireActivity(), MainActivity::class.java)
                 startActivity(intent)
                 requireActivity().finish()
            }
        }
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            // continue
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
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
                // continue
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
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

        // TODO: Add the following permissions
        // Schedule exact alarm
        // private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.SCHEDULE_EXACT_ALARM)
    }
}