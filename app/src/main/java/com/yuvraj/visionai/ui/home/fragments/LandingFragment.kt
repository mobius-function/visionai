package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.EyeTestsList
import com.yuvraj.visionai.data.EyeTestMenuList.getEyeTestMenuList
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding
import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants.ASTIGMATISM
import com.yuvraj.visionai.utils.Constants.DEBUG_TOGGLE
import com.yuvraj.visionai.utils.Constants.DRY_EYE
import com.yuvraj.visionai.utils.Constants.GET_STARTED
import com.yuvraj.visionai.utils.Constants.HYPEROPIA
import com.yuvraj.visionai.utils.Constants.MYOPIA
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS
import com.yuvraj.visionai.utils.Constants.USER_PROFILE
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateEyeHealthScore
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getLastEyeTestDate
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.isDebugMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setDebugMode
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LandingFragment : Fragment(R.layout.fragment_home_landing) {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    private var _eyeHealthScore: Float? = null
    private val eyeHealthScore get() = _eyeHealthScore!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        checkForPermission()
        clickableViews()
        setAlertView(eyeHealthScore > 60)
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeLandingBinding.bind(view)
        _eyeHealthScore = requireActivity().calculateEyeHealthScore()

        setEyeHealthScore(eyeHealthScore)

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

            GET_STARTED -> {
                // call showShowcase() defined in MainActivity
                super.getActivity()?.let {
                    (it as MainActivity).showShowcase()
                }
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

    private fun setAlertView(isGoodScore : Boolean) {
        binding.apply{
            // No last test recorded
            if(requireActivity().getLastEyeTestDate() == "") {
                setEyeHealthScore(0f)
                alertView.apply {
                    layout.background = requireActivity().getDrawable(R.drawable.bg_alert_red)
                    tvTitle.text = "No Test Recorded!"
                    tvContent.text = "You have not taken any eye test yet." +
                            "\n\nWe recommend you to take a full eye test for updated result."

                    btnTest.apply {
                        setBackgroundColor(requireActivity().getColor(R.color.red_button))
                        visibility = View.VISIBLE
                        setOnClickListener {
                            findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
                        }
                    }
                }
                return
            }


            else if(isGoodScore) {
                alertView.apply {
                    layout.background = requireActivity().getDrawable(R.drawable.bg_alert_green)
                    tvTitle.text = "Good Eye Health!"
                    tvContent.text = "Your eye health score is ${eyeHealthScore.toInt()}!" +
                            "\n\nKeep it up! You are doing great. Continue to take care of your eyes."

                    btnTest.apply {
                        setBackgroundColor("#c0faa2".toColorInt())
                        visibility = View.GONE
                        setOnClickListener {
                            // do nothing
                        }
                    }
                }
            } else {
                alertView.apply {
                    layout.background = requireActivity().getDrawable(R.drawable.bg_alert_red)
                    tvTitle.text = "Poor Eye Health!"
                    tvContent.text = "Your eye health score is ${eyeHealthScore.toInt()}!" +
                            "\n\nYou need to take care of your eyes. We recommend you to take a full eye test for further updated result."

                    btnTest.apply {
                        setBackgroundColor(requireActivity().getColor(R.color.red_button))
                        visibility = View.VISIBLE
                        setOnClickListener {
                            findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
                        }
                    }
                }
            }

        }
    }


    private fun setEyeHealthScore(score: Float) {
        binding.apply {
            circularProgressBar.apply {
                progress = 0f
                setProgressWithAnimation(score * 0.8f, 1000)
            }

            waterWaveView.apply {
                progress = score.toInt()
                setAnimationSpeed(50)
                setBehindWaveColor("#254015".toColorInt())
                setFrontWaveColor("#1d4009".toColorInt())
                startAnimation()
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