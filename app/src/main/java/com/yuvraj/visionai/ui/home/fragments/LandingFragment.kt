package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.EyeTestsList
import com.yuvraj.visionai.data.EyeTestMenuList.getEyeTestMenuList
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding
import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.utils.Constants.ASTIGMATISM
import com.yuvraj.visionai.utils.Constants.DEBUG_TOGGLE
import com.yuvraj.visionai.utils.Constants.DRY_EYE
import com.yuvraj.visionai.utils.Constants.HYPEROPIA
import com.yuvraj.visionai.utils.Constants.MYOPIA
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS
import com.yuvraj.visionai.utils.Constants.USER_PROFILE
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.isDebugMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setDebugMode
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : Fragment(R.layout.fragment_home_landing) {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

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

        viewModel.apply {
            clearTestResults()
        }
    }

    private fun clickableViews() {
        binding.apply {

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

            USER_PROFILE -> {
                findNavController().navigate(R.id.profileFragment)
            }

            DEBUG_TOGGLE -> {
                if(requireActivity().isDebugMode()) {
                    requireActivity().setDebugMode(false)
                    Toast.makeText(requireActivity(), "Debug Mode Disabled", Toast.LENGTH_SHORT).show()
                } else {
                    requireActivity().setDebugMode(true)
                    Toast.makeText(requireActivity(), "Debug Mode Enabled", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                Toast.makeText(requireActivity(), "Available in Next Update!!!", Toast.LENGTH_SHORT).show()
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
    }
}