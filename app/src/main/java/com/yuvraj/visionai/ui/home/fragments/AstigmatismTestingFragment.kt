package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeAstigmatismTestingBinding
import com.yuvraj.visionai.enums.FaceStatus
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.DebugTags
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AstigmatismTestingFragment : Fragment(R.layout.fragment_home_astigmatism_testing) {
    private var _binding: FragmentHomeAstigmatismTestingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()

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
        createCameraManager()
        checkForPermission()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeAstigmatismTestingBinding.bind(view)

        isAllInOneTest = requireActivity().getAllInOneEyeTestMode()
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

        var totalTimeSpent = (System.currentTimeMillis() - fragmentStartTime)/1000

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

            // get current data in format (DD/MM/YYYY) and time in format (HH:MM:SS) as id
            val id = Calendar.getInstance().time.toString()

            // total time spent in minutes
            val totalTimeSpentInMinutes : Double = sharedPreferences.getLong(Constants.TOTAL_TIME_SPENT_TESTING, 0).toDouble() / 60
            val totalLeftEyePartialBlinkCounter = sharedPreferences.getInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
            val totalRightEyePartialBlinkCounter = sharedPreferences.getInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)

            val myopiaResultsLeftEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_LEFT_EYE, 0.0f)
            val myopiaResultsRightEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_RIGHT_EYE, 0.0f)

            val hyperopiaResultsLeftEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_LEFT_EYE, 0.0f)
            val hyperopiaResultsRightEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_RIGHT_EYE, 0.0f)

            val dryLeftEyeResults = totalLeftEyePartialBlinkCounter/totalTimeSpentInMinutes > 10
            val dryRightEyeResults = totalRightEyePartialBlinkCounter/totalTimeSpentInMinutes > 10

            Log.d("DebugTimeResult", "The total time spent is: $totalTimeSpent minutes " +
                    "$totalLeftEyePartialBlinkCounter $totalRightEyePartialBlinkCounter")

            val eyeTestResult = EyeTestResult(
                id = id,
                astigmatismResult = astigmatismResults,
                dryLeftEyeResult = dryLeftEyeResults,
                dryRightEyeResult = dryRightEyeResults,
                jaundiceResult = false,
                plusPowerLeftEye = hyperopiaResultsLeftEye,
                plusPowerRightEye = hyperopiaResultsRightEye,
                minusPowerLeftEye = myopiaResultsLeftEye,
                minusPowerRightEye = myopiaResultsRightEye
            )

            // Log.d("DebugEyeTestResult", eyeTestResult.toString())

            viewModel.saveEyeTest(eyeTestResult)
        }

        findNavController().navigate(R.id.generatedResultFragment)

        // TODO: "Show an alert dialog with the result of the test."
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