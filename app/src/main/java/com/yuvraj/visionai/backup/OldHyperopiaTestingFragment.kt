package com.yuvraj.visionai.backup

import android.content.Context.MODE_PRIVATE
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
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.enums.FaceStatus
import com.yuvraj.visionai.utils.Constants.MAX_DISPLAYED_TEXT_SIZE
import com.yuvraj.visionai.utils.Constants.MAX_READINGS
import com.yuvraj.visionai.utils.Constants.MIN_DISPLAYED_TEXT_SIZE
import com.yuvraj.visionai.utils.Constants.USER_AGE
import com.yuvraj.visionai.utils.Constants.USER_DETAILS
import com.yuvraj.visionai.utils.DebugTags.FACE_DETECTION
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateFocalLength
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculatePositivePower
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastHyperopiaResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.updateAllInOneEyeTestModeAfterTest
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OldHyperopiaTestingFragment : Fragment(R.layout.fragment_home_eye_testing) {

    private var _binding: FragmentHomeEyeTestingBinding? = null
    private val binding get() = _binding!!

    private val fragmentStartTime : Long = System.currentTimeMillis()

    private val viewModel: UserViewModel by activityViewModels()

    private var isAllInOneTest: Boolean = false

    private lateinit var cameraManager: CameraManager

    //    private var focalLengthFound : Double = requireActivity().calculateFocalLength()
    private val realFaceWidth : Double = 14.0

    private var distanceCurrent : Float = 0.0f
    private var distanceMaximum : Float = 350.0f
    private var baseDistance:Float = 350.0f         // in mm

    private  var textSize: Float = 2.0f
    private  var relativeTextSize: Float = 1.0f

    private var reading : Int = 0
    private var score : Int = 0

    private var lastCorrect: Float? = null
    private var lastIncorrect: Float? = null
    private var checkingRightEye: Boolean? = false

    // for dry eye testing
    private var leftEyePartialBlinkCounter: Int = 0
    private var rightEyePartialBlinkCounter: Int = 0

    private var hyperopiaLeftEyePower: Float = 0.0f
    private var hyperopiaRightEyePower: Float = 0.0f

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
        AlertDialogBox.showInstructionDialogBox(
            requireActivity(),
            "Follow me!",
            message
        )

        binding.tvInstructions.text = message

        Log.e("EyeTesting Debug","The initial text size in MM is: $textSize mm")

        val r = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM, textSize,
            getResources().getDisplayMetrics())

        Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
        displayRandomText(textSize)

        baseDistance = 350.0f
        distanceMaximum = distanceCurrent

        relativeTextSize = textSize * (baseDistance/distanceMaximum)
    }

    private fun debug(debugMode: Boolean) {
        binding.apply {
            if(debugMode) {
                tvLeftEye.visibility = View.VISIBLE
                tvRightEye.visibility = View.VISIBLE
                tvCurrentDistance.visibility = View.VISIBLE
                tvMinimumDistance.visibility = View.VISIBLE
            } else {
                tvLeftEye.visibility = View.INVISIBLE
                tvRightEye.visibility = View.INVISIBLE
                tvCurrentDistance.visibility = View.INVISIBLE
                tvMinimumDistance.visibility = View.INVISIBLE
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
            Log.e("EyeTesting Debug","The initial text size in DP is: $textSizeDisplay dp")
            tvRandomText.text = textDisplay
        }
    }

    private fun onCheck(correctResult : Boolean) {

        Log.e("EyeTesting Debug", "${binding.tvRandomText.text} ${binding.tvInput.text} $correctResult")

        binding.tvInput.setText("")

        reading += 1

        relativeTextSize = textSize * (baseDistance/distanceMaximum)

        if(correctResult) {
            score += 1
            lastCorrect = relativeTextSize

            if(lastIncorrect == null) {
                textSize = relativeTextSize*2
            } else {
                textSize = (relativeTextSize + lastIncorrect!!)/2
            }

            Log.e("EyeTesting Debug", "Correct!")
        }

        else {
            lastIncorrect = relativeTextSize

            if(lastCorrect == null) {
                textSize = relativeTextSize / 2
            } else {
                textSize = (relativeTextSize + lastCorrect!!)/2
            }

            Log.e("EyeTesting Debug", "Incorrect!")
        }

        if(reading <= MAX_READINGS && textSize < MAX_DISPLAYED_TEXT_SIZE && textSize > MIN_DISPLAYED_TEXT_SIZE) {
            displayRandomText(textSize)
            Log.e("EyeTesting Debug","The presented text size in MM is: $textSize mm")
        }

        else {
            var diapter : Float = 0.0f
            if(lastIncorrect == null) {
                diapter = 0.0f
            } else {
                val sharedPreferences = requireActivity().getSharedPreferences(USER_DETAILS, MODE_PRIVATE)
                val userAge = sharedPreferences.getInt(USER_AGE, 20)
                diapter = calculatePositivePower(lastIncorrect!!, userAge, baseDistance)
            }
            binding.tvRandomText.setTextSize(TypedValue.COMPLEX_UNIT_MM, 10.0f)
            binding.tvRandomText.text = "$diapter"
            Toast.makeText(requireActivity(), "Your score is $score and deno is: $diapter", Toast.LENGTH_SHORT).show()

            if(checkingRightEye == false){
                checkingRightEye = true
                baseDistance = 350.0f
                distanceMaximum = distanceCurrent

                textSize = 2.0f
                relativeTextSize = 1.0f

                reading = 0
                score = 0

                lastCorrect = null
                lastIncorrect = null

                val message : String = "Now Cover Right eye with your Right hand, " +
                        "ensure to avoid applying pressure to the eyelid. " +
                        "Read the letters on the screen and Input what you see in the Input field."

                binding.tvInstructions.text = message

                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Follow Instruction!",
                    message
                )

                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Positive Power",
                    "Your power of Left eye is $diapter"
                )

                hyperopiaLeftEyePower = diapter

                val r = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_MM, textSize,
                    resources.displayMetrics
                )

                Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
                displayRandomText(textSize)

                baseDistance = 350.0f
                distanceMaximum = distanceCurrent

                relativeTextSize = textSize * (baseDistance/distanceMaximum)

            } else {
                //EVENT: End of the test
                val totalTimeSpent : Long = (System.currentTimeMillis() - fragmentStartTime)/1000

                AlertDialogBox.showInstructionDialogBox(
                    requireActivity(),
                    "Positive Power",
                    "Your power of Right eye is $diapter"
                )

                hyperopiaRightEyePower = diapter

                if(isAllInOneTest) {

                    requireActivity().updateAllInOneEyeTestModeAfterTest(
                        totalTimeSpent,
                        leftEyePartialBlinkCounter,
                        rightEyePartialBlinkCounter
                    )

                    viewModel.apply {
                        updatePlusPowerLeftEye(hyperopiaLeftEyePower)
                        updatePlusPowerRightEye(hyperopiaRightEyePower)
                    }

                    // Log.d("DebugEyeTests", "Saved EyeTest after Hyperopia: ${viewModel.eyeTestResult}")

                    // Log.d("DebugTimeResult", "The total time spent is: $totalTimeSpent minutes " +
                    // "$leftEyePartialBlinkCounter $rightEyePartialBlinkCounter")

                    requireActivity().setPastHyperopiaResults(hyperopiaLeftEyePower, hyperopiaRightEyePower)

                    findNavController().navigate(
                        R.id.action_hyperopiaTestingFragment_to_astigmatismTestingFragment
                    )
                } else {
                    // TODO : Suggest if the user needs to do another test
                    requireActivity().setPastHyperopiaResults(hyperopiaLeftEyePower, hyperopiaRightEyePower)
                    // TODO : Show the test results
                }
            }
        }

        distanceMaximum = distanceCurrent
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
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

        // Debugging the camera details for the device (Open Debug console)
        cameraManager.getCameraDetails(requireActivity())
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e(FACE_DETECTION,"This is it ${faceStatus.name}")
//        tvFaceWidth.text
//       when(faceStatus){}
    }

    private fun updateTVFaceWidth(face: Face, lEOP: Float, rEOP: Float) {
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
        binding.tvCurrentDistance.text = "Current Distance: ${distanceCurrent} cm"

        if(distanceCurrent > distanceMaximum) {
            distanceMaximum = distanceCurrent
            binding.tvMinimumDistance.text = "Maximum Distance: ${distanceMaximum} cm"
        }

        if (rEOP in 0.4..0.7 ) {
            rightEyePartialBlinkCounter += 1
            binding.tvRightEye.text = "RE Partial Blink: $rightEyePartialBlinkCounter"
        }


        if (lEOP in 0.4..0.7 ) {
            leftEyePartialBlinkCounter += 1
            binding.tvLeftEye.text = "LE Partial Blink: $leftEyePartialBlinkCounter"
        }

        // Log.e(FACE_DETECTION,"The left eye open probability is: $lEOP")
        // Log.e(FACE_DETECTION,"The right eye open probability is: $rEOP")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        Log.e("OnDestroy", "onDestroy: HyperopiaTestingFragment")
        super.onDestroy()
        _binding = null
    }
}