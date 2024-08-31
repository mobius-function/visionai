package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeEyeTestingBinding
import com.yuvraj.visionai.enums.FaceStatus
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.Constants.REQUIRED_PERMISSIONS_FOR_CAMERA
import com.yuvraj.visionai.utils.DebugTags.FACE_DETECTION
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateFocalLength
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateNegativePower
import com.yuvraj.visionai.utils.clients.AlertDialogBox.Companion.showInstructionDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [EyeTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class EyeTestingFragment : Fragment(R.layout.fragment_home_eye_testing) {

    private var _binding: FragmentHomeEyeTestingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    private val fragmentStartTime : Long = System.currentTimeMillis()

    private var isAllInOneTest: Boolean = false

    private lateinit var cameraManager: CameraManager

//    private val focalLengthFound : Double = requireActivity().calculateFocalLength()
    private val realFaceWidth : Double = 14.0

    private var distanceCurrent : Float = 0.0f
    private var distanceMinimum : Float = 350.0f
    private var baseDistance:Float = 350.0f

    private  var textSize: Float = 1.0f
    private  var relativeTextSize: Float = 1.0f

    private var reading : Int = 0
    private var score : Int = 0

    private var lastCorrect: Float? = null
    private var lastIncorrect: Float? = null
    private var checkingRightEye: Boolean? = false

    // for dry eye testing
    private var leftEyePartialBlinkCounter: Int = 0
    private var rightEyePartialBlinkCounter: Int = 0

    private var myopiaLeftEyePower: Float = 0.0f
    private var myopiaRightEyePower: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        debug(false)
        createCameraManager()
        checkForPermission()
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeEyeTestingBinding.bind(view)

        isAllInOneTest = requireActivity().getAllInOneEyeTestMode()

        val message: String = "Cover left eye with your left hand, ensure to avoid applying pressure to the eyelid. Read the letters on the screen beginning at the top. Once completed, repeat with the right eye."
        showInstructionDialogBox(
            requireActivity(),
            "Follow me!",
            message
        )

        binding.tvInstructions.text = message

        Log.e("EyeTesting Debug","The initial text size in MM is: $textSize mm")


        val r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
            getResources().getDisplayMetrics())
        Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
        displayRandomText(textSize)

        baseDistance = 350.0f

        distanceMinimum = 1000.0f
        relativeTextSize = textSize * (baseDistance/distanceMinimum)
    }

    private fun debug(debugMode: Boolean) {
        if(debugMode) {
            binding.apply {
                tvLeftEye.visibility = View.INVISIBLE
                tvRightEye.visibility = View.INVISIBLE
                tvCurrentDistance.visibility = View.INVISIBLE
                tvMinimumDistance.visibility = View.INVISIBLE
            }
        } else {
            binding.apply {
                tvLeftEye.visibility = View.VISIBLE
                tvRightEye.visibility = View.VISIBLE
                tvCurrentDistance.visibility = View.VISIBLE
                tvMinimumDistance.visibility = View.VISIBLE
            }
        }
    }

    private fun clickableViews() {

        binding.apply {
            btnCheck.setOnClickListener {
                onCheck(tvRandomText.text.toString().lowercase() ==
                        tvInput.text.toString().lowercase())
            }

            btnUnclearText.setOnClickListener {
                onCheck(false)
            }

            tvInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    tvInstructions.visibility = View.GONE
                } else {
                    tvInstructions.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun displayRandomText(textSizeDisplay: Float) {
        val textDisplay : String = (((0..25).random() + 65).toChar()).toString()

        binding.apply {
            tvRandomText.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeDisplay)
            tvRandomText.text = textDisplay
        }
    }


    private fun onCheck(correctResult : Boolean) {

        binding.tvInput.setText("")

        reading += 1

        relativeTextSize = textSize * (baseDistance/distanceMinimum)

        if(correctResult) {
            score += 1

            lastCorrect = relativeTextSize
            if(lastIncorrect == null) {
                textSize = relativeTextSize/2
            } else {
                textSize = (relativeTextSize + lastIncorrect!!)/2
            }

            Toast.makeText(requireActivity(), "Correct", Toast.LENGTH_SHORT).show()
        }

        else {
            lastIncorrect = relativeTextSize
            if(lastCorrect == null) {
                textSize = relativeTextSize * 2
            } else {
                textSize = (relativeTextSize + lastCorrect!!)/2
            }

            Toast.makeText(requireActivity(), "Incorrect", Toast.LENGTH_SHORT).show()
        }

        if(reading <= 6 && textSize > 0.25f) {
            displayRandomText(textSize)
            Log.e("EyeTesting Debug","The presented text size in MM is: $textSize mm")
            Log.e("EyeTesting Debug","The presented Base Distance in MM is: $baseDistance mm")
            Log.e("EyeTesting Debug","The presented Distance Minimum in MM is: $distanceMinimum mm")
            Log.e("EyeTesting Debug","The presented Distance Current in MM is: $distanceCurrent mm")
        } else {
            var deno : Double?

            deno = if(lastCorrect != null) {
                (lastCorrect!! * 20)/0.50905435
            } else {
                (lastIncorrect!! * 20)/0.50905435
            }

            if(checkingRightEye == false){
                checkingRightEye = true
                baseDistance = 350.0f
                distanceMinimum = distanceCurrent

                textSize = 1.0f
                relativeTextSize = 1.0f

                reading = 0
                score = 0

                lastCorrect = null
                lastIncorrect = null

                val message : String = "Now Cover Right eye with your Right hand, " +
                        "ensure to avoid applying pressure to the eyelid. " +
                        "Read the letters on the screen and Input what you see in the Input field."

                binding.tvInstructions.text = message

                showInstructionDialogBox(
                    requireActivity(),
                    "Follow Instruction!",
                    message
                )

                myopiaLeftEyePower = calculateNegativePower(deno)

                showInstructionDialogBox(
                    requireActivity(),
                    "Negative Power",
                    "Your power of left eye is $myopiaLeftEyePower"
                )

                val r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
                    resources.displayMetrics
                )

                Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
                displayRandomText(textSize)

                baseDistance = 350.0f
                distanceMinimum = distanceCurrent

                relativeTextSize = textSize * (baseDistance/distanceMinimum)

            } else {
                // EVENT: End of the test
                val totalTimeSpent : Long = (System.currentTimeMillis() - fragmentStartTime)/1000

                myopiaRightEyePower = calculateNegativePower(deno)

                showInstructionDialogBox(
                    requireActivity(),
                    "Negative Power",
                    "Your power of right eye is $myopiaRightEyePower"
                )
                isAllInOneTest = requireActivity().getAllInOneEyeTestMode()
                if(isAllInOneTest) {

                    requireActivity().updateAllInOneEyeTestModeAfterTest(
                        totalTimeSpent,
                        leftEyePartialBlinkCounter,
                        rightEyePartialBlinkCounter
                    )

                    viewModel.apply {
                        updateMinusPowerLeftEye(myopiaLeftEyePower)
                        updateMinusPowerRightEye(myopiaRightEyePower)
                    }

                    // Log.d("DebugEyeTests", "Saved EyeTest after Myopia: ${viewModel.eyeTestResult}")

                    // Log.d("DebugTimeResult", "The total time spent is: $totalTimeSpent minutes " +
                            // "$leftEyePartialBlinkCounter $rightEyePartialBlinkCounter")

                    findNavController().navigate(
                        R.id.action_eyeTestingFragment_to_hyperopiaTestingFragment
                    )
                } else {
                    // TODO : Suggest if the user needs to do another test

                    // TODO : Show the test results
                }
            }
        }

        distanceMinimum = distanceCurrent
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS_FOR_CAMERA,
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
                cameraManager.startCamera()
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()

                requireActivity().finish()
            }
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
        Log.e(FACE_DETECTION,"This is it ${faceStatus.name}")
    }

    private fun updateTVFaceWidth(face: Face, lEOP : Float, rEOP : Float) {
        val faceWidth : Int = DistanceHelper.pixelsToDp(face.boundingBox.width()).toInt()
        var distance = 0.0

        if(faceWidth != 0) {
            distance = DistanceHelper.distanceFinder(
                requireActivity().calculateFocalLength(),
                realFaceWidth,
                faceWidth.toDouble()
            )
        }

        distanceCurrent = distance.toFloat()*100.0f
        binding.tvCurrentDistance.text = "Current Distance: ${distanceCurrent/10} cm"

        if(distanceCurrent < distanceMinimum) {
            distanceMinimum = distanceCurrent
            binding.tvMinimumDistance.text = "Minimum Distance: ${distanceMinimum/10} cm"
        }

        if (rEOP in 0.4..0.7 ) {
            rightEyePartialBlinkCounter += 1
            binding.tvRightEye.text = "RE Partial Blink: $rightEyePartialBlinkCounter"
        }


        if (lEOP in 0.4..0.7 ) {
            leftEyePartialBlinkCounter += 1
            binding.tvLeftEye.text = "LE Partial Blink: $leftEyePartialBlinkCounter"
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS_FOR_CAMERA.all {
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