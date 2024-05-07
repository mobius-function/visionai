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
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeEyeTestingBinding
import com.yuvraj.visionai.model.FaceStatus
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.utils.DebugTags.FACE_DETECTION
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateFocalLength
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateNegativePower
import com.yuvraj.visionai.utils.clients.AlertDialogBox.Companion.showInstructionDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setAllInOneEyeTestMode
import java.util.*
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 * Use the [EyeTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EyeTestingFragment : Fragment(R.layout.fragment_home_eye_testing) {

    private var _binding: FragmentHomeEyeTestingBinding? = null
    private val binding get() = _binding!!

    private val fragmentStartTime : Long = System.currentTimeMillis()

    private var isAllInOneTest: Boolean = false

    private lateinit var cameraManager: CameraManager

    private val focalLengthFound : Double = calculateFocalLength()
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
    private var leftEyeFullBlinkCounter: Int = 0
    private var rightEyeFullBlinkCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
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

        createCameraManager()

        distanceMinimum = 1000.0f
        relativeTextSize = textSize * (baseDistance/distanceMinimum)
    }

    private fun clickableViews() {

        binding.apply {
            btnCheck.setOnClickListener {
                onCheck(tvRandomText.text.toString().lowercase() ==
                        tvInput.text.toString().lowercase())
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
            var deno : Double? = null

            if(lastCorrect != null) {
                deno = (lastCorrect!! * 20)/0.50905435
            } else {
                deno = (lastIncorrect!! * 20)/0.50905435
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

                showInstructionDialogBox(
                    requireActivity(),
                    "Negative Power",
                    "Your power of left eye is ${calculateNegativePower(deno)}"
                )

                val r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
                    getResources().getDisplayMetrics())

                Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
                displayRandomText(textSize)

                baseDistance = 350.0f
                distanceMinimum = distanceCurrent

                relativeTextSize = textSize * (baseDistance/distanceMinimum)

            } else {
                // EVENT: End of the test

                val totalTimeSpent = (System.currentTimeMillis() - fragmentStartTime)/1000

                showInstructionDialogBox(
                    requireActivity(),
                    "Negative Power",
                    "Your power of right eye is ${calculateNegativePower(deno)}"
                )

                if(isAllInOneTest) {
                    findNavController().navigate(
                        R.id.action_eyeTestingFragment_to_hyperopiaTestingFragment
                    )
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
                focalLengthFound,
                realFaceWidth,
                faceWidth.toDouble()
            )
        }

        distanceCurrent = distance.toFloat()*100.0f
        binding.tvCurrentDistance.text = "Current Distance: ${distanceCurrent} cm"

        if(distanceCurrent < distanceMinimum) {
            distanceMinimum = distanceCurrent
            binding.tvMinimumDistance.text = "Minimum Distance: ${distanceMinimum} cm"
        }

        if (rEOP in 0.4..0.7 ) {
            binding.tvRightEye.text = "Partial Blink"
        } else if (rEOP < 0.3) {
            binding.tvRightEye.text = "Full Blink"
        } else {
            binding.tvRightEye.text = "Does not Blink"
        }


        if (lEOP in 0.4..0.7 ) {
            binding.tvLeftEye.text = "Partial Blink"
        } else if (lEOP < 0.3) {
            binding.tvLeftEye.text = "Full Blink"
        } else {
            binding.tvLeftEye.text = "Does not Blink"
        }

        Log.e(FACE_DETECTION,"The left eye open probability is: $lEOP")
        Log.e(FACE_DETECTION,"The right eye open probability is: $rEOP")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        Log.e("OnDestroy", "onDestroy: EyeTestingFragment")
        super.onDestroy()
        _binding = null
    }
}