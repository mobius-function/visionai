package com.yuvraj.visionai.ui.aioEyeTest.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.databinding.FragmentHomeProfileBinding
import com.yuvraj.visionai.model.FaceStatus
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS_FOR_CAMERA
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS_FOR_NOTIFICATIONS_AND_ALARM
import com.yuvraj.visionai.utils.DebugTags
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest

/**
 * A simple [Fragment] subclass.
 * Use the [AstigmatismTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AstigmatismTestingFragment : Fragment(R.layout.fragment_home_astigmatism_testing) {
    private var _binding: FragmentHomeAstigmatismTestingBinding? = null
    private val binding get() = _binding!!

    private val fragmentStartTime : Long = System.currentTimeMillis()

    private var isAllInOneTest: Boolean = false

    // for dry eye testing
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
        createCameraManager()
        clickableViews()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeAstigmatismTestingBinding.bind(view)

        isAllInOneTest = requireActivity().getAllInOneEyeTestMode()

        // TODO: Implement the logic for the fragment
        if (isAllInOneTest) {

        }

        else {

        }
    }

    private fun clickableViews() {
        binding.apply {
            btnYes.setOnClickListener {
                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Result!",
                    "You have Astigmatism. Please consult a doctor."
                )
            }

            btnNo.setOnClickListener {
                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Result!",
                    "You don't have Astigmatism."
                )
            }
        }
    }

    private fun endFunction() {
        val totalTimeSpent : Long = (System.currentTimeMillis() - fragmentStartTime)/1000

        if (isAllInOneTest) {
            requireActivity().updateAllInOneEyeTestModeAfterTest(
                totalTimeSpent,
                leftEyePartialBlinkCounter,
                rightEyePartialBlinkCounter
            )

            // TODO : navigate to other test

        }

        else {
            // TODO : Suggest if the user needs to do another test

            // TODO : Show the test results

        }
    }

    private fun createCameraManager() {
        if(cameraPermissionGranted()) {
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

    private fun cameraPermissionGranted() = REQUIRED_PERMISSIONS_FOR_CAMERA.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        Log.e("OnDestroy", "onDestroy: EyeTestingFragment")
        super.onDestroy()
        _binding = null
    }
}