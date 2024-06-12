package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.enums.FaceStatus
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.DebugTags
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest


class AstigmatismTestingFragment : Fragment(R.layout.fragment_home_astigmatism_testing) {
    private var _binding: FragmentHomeAstigmatismTestingBinding? = null
    private val binding get() = _binding!!

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
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeAstigmatismTestingBinding.bind(view)
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

    private fun check(result: Boolean) {
        val message: String = if (result) {
            "You have Astigmatism. Please consult a doctor."
        } else {
            "You don't have Astigmatism."
        }

        AlertDialogBox.showInstructionDialogBox(
            requireActivity(),
            "Result!",
            message
        )

        val totalTimeSpent : Long = (System.currentTimeMillis() - fragmentStartTime)/1000

        if(isAllInOneTest) {

            requireActivity().updateAllInOneEyeTestModeAfterTest(
                totalTimeSpent,
                leftEyePartialBlinkCounter,
                rightEyePartialBlinkCounter
            )

            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.EYE_TEST_RESULTS,
                AppCompatActivity.MODE_PRIVATE
            )

            val totalTimeSpent = sharedPreferences.getLong(Constants.TOTAL_TIME_SPENT_TESTING, 0)
            val totalLeftEyePartialBlinkCounter = sharedPreferences.getInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
            val totalRightEyePartialBlinkCounter = sharedPreferences.getInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)

            val myopiaResultsLeftEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_LEFT_EYE, 0.0f)
            val myopiaResultsRightEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_RIGHT_EYE, 0.0f)

            val hyperopiaResultsLeftEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_LEFT_EYE, 0.0f)
            val hyperopiaResultsRightEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_RIGHT_EYE, 0.0f)

            val astigmatismResults = result
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

    override fun onDestroy() {
        Log.e("OnDestroy", "onDestroy: EyeTestingFragment")
        super.onDestroy()
        _binding = null
    }
}