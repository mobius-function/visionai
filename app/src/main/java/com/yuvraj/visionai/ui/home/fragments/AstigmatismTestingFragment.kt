package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.enums.FaceStatus
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.DebugTags
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.isDebugMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastAstigmatismResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastDryEyeResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AstigmatismTestingFragment : Fragment(R.layout.fragment_home_astigmatism_testing) {
    private var _binding: FragmentHomeAstigmatismTestingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    private val fragmentStartTime : Long = System.currentTimeMillis()

    private var isAllInOneTest: Boolean = false

    private lateinit var cameraManager: CameraManager

    private var leftEyePartialBlinkCounter: Int = 0
    private var rightEyePartialBlinkCounter: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
        debug(requireActivity().isDebugMode())
        createCameraManager()
        checkForPermission()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeAstigmatismTestingBinding.bind(view)

        isAllInOneTest = requireActivity().getAllInOneEyeTestMode()
    }

    private fun debug(isDebugMode: Boolean) {
        if(isDebugMode) {
            binding.tvLeftEye.visibility = View.VISIBLE
            binding.tvRightEye.visibility = View.VISIBLE
        } else {
            binding.tvLeftEye.visibility = View.GONE
            binding.tvRightEye.visibility = View.GONE
        }
    }

    private fun clickableViews() {
        binding.apply {
            btnYes.setOnClickListener {
                check(true)
            }

            btnNo.setOnClickListener {
                check(false)
            }
        }
    }

    private fun check(astigmatismResults: Boolean) {
        val message: String = if (astigmatismResults) {
            "You have Astigmatism. Please consult a doctor."
        } else {
            "You don't have Astigmatism."
        }

        AlertDialogBox.showInstructionDialogBox(
            requireActivity(),
            "Result!",
            message
        )

        val totalTimeSpent = (System.currentTimeMillis() - fragmentStartTime)/1000

        if(isAllInOneTest) {

            // Log.d("DebugEyeTestResult", "The total time spent is: $totalTimeSpent")
            // Log.d("DebugEyeTestResult", "The left eye partial blink counter is: $leftEyePartialBlinkCounter")
            // Log.d("DebugEyeTestResult", "The right eye partial blink counter is: $rightEyePartialBlinkCounter")

            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.EYE_TEST_RESULTS,
                AppCompatActivity.MODE_PRIVATE
            )

            requireActivity().updateAllInOneEyeTestModeAfterTest(
                totalTimeSpent,
                leftEyePartialBlinkCounter,
                rightEyePartialBlinkCounter
            )

            // total time spent in minutes
            val totalTimeSpentInMinutes : Double = sharedPreferences.getLong(Constants.TOTAL_TIME_SPENT_TESTING, 0).toDouble() / 60
            val totalLeftEyePartialBlinkCounter = sharedPreferences.getInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
            val totalRightEyePartialBlinkCounter = sharedPreferences.getInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)

            viewModel.apply {
                updateAstigmatismResult(astigmatismResults)
                updateJaundiceResult(false)
                updateDryLeftEyeResult(totalLeftEyePartialBlinkCounter/totalTimeSpentInMinutes > 10)
                updateDryRightEyeResult(totalRightEyePartialBlinkCounter/totalTimeSpentInMinutes > 10)
            }

            // Log.d("DebugEyeTests", "Saved EyeTest after Astigmatism: ${viewModel.eyeTestResult}")

            // Log.d("DebugTimeResult", "The total time spent is: $totalTimeSpent minutes " +
                    // "$totalLeftEyePartialBlinkCounter $totalRightEyePartialBlinkCounter")

            viewModel.saveEyeTest()

            requireActivity().apply {
                setPastAstigmatismResults(astigmatismResults)
                setPastDryEyeResults(totalLeftEyePartialBlinkCounter/totalTimeSpentInMinutes > 10,
                    totalRightEyePartialBlinkCounter/totalTimeSpentInMinutes > 10)
            }

            findNavController().navigate(R.id.generatedResultFragment)
        }

        else {
            findNavController().navigate(R.id.landingFragment)
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            requireActivity(),
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            ::processPicture,
            ::updateTVFaceWidth
        )

        // Debugging the device info for the camera (Open Debug console)
        cameraManager.getCameraDetails(requireActivity())
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.REQUIRED_PERMISSIONS_FOR_CAMERA,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e(DebugTags.FACE_DETECTION,"This is it ${faceStatus.name}")
    }

    private fun updateTVFaceWidth(face: Face, lEOP : Float, rEOP : Float) {
        if (rEOP in 0.4..0.7 ) {
            rightEyePartialBlinkCounter += 1
            binding.tvRightEye.text = "RE Partial Blink: $rightEyePartialBlinkCounter"
        }


        if (lEOP in 0.4..0.7 ) {
            leftEyePartialBlinkCounter += 1
            binding.tvLeftEye.text = "LE Partial Blink: $leftEyePartialBlinkCounter"
        }

        Log.e(DebugTags.FACE_DETECTION,"The left eye open probability is: $lEOP")
        Log.e(DebugTags.FACE_DETECTION,"The right eye open probability is: $rEOP")
    }

    private fun allPermissionsGranted() = Constants.REQUIRED_PERMISSIONS_FOR_CAMERA.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onDestroy() {
        Log.e("OnDestroy", "onDestroy: EyeTestingFragment")
        super.onDestroy()
        _binding = null
    }
}