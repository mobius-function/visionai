package com.yuvraj.visionai.ui.home.fragments

import android.speech.RecognizerIntent
import java.util.*
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeEyeTestingBinding
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CaptureRequest
import androidx.camera.camera2.internal.compat.CameraManagerCompat
import android.hardware.camera2.CameraManager as CameraManager1
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.service.faceDetection.FaceStatus
import com.yuvraj.visionai.utils.PowerAlgorithm
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateFocalLength
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.calculateNegativePower
import com.yuvraj.visionai.utils.clients.AlertDialogBox.Companion.showInstructionDialogBox
import com.yuvraj.visionai.utils.helpers.DistanceHelper

/**
 * A simple [Fragment] subclass.
 * Use the [EyeTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EyeTestingFragment : Fragment(R.layout.fragment_home_eye_testing) {

    private var _binding: FragmentHomeEyeTestingBinding? = null
    private val binding get() = _binding!!

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

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(requireActivity()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.US
            }
        }
    }

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
                showInstructionDialogBox(
                    requireActivity(),
                    "Negative Power",
                    "Your power of right eye is ${calculateNegativePower(deno)}"
                )
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

        // get focal length of the camera
//        val focalLengthKey = CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
//        val focalLengths = cameraManager.getCameraCharacteristics().get(focalLengthKey)

        val cameraManager1 = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager1
//        val cameraManager1 = getSystemService(requireActivity().applicationContext.C) as CameraManager


        try {
            // Get the list of available camera IDs
            val cameraIds = cameraManager1.cameraIdList

            for (cameraId in cameraIds) {
                // Get the camera characteristics for the specified camera ID
                val characteristics = cameraManager1.getCameraCharacteristics(cameraId)

                // Get the focal lengths array
                val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)

                // Print the focal lengths
                for (focalLength in focalLengths ?: floatArrayOf()) {
                    // Update your UI or log the focal lengths as needed
                    Log.e("focalLength","Camera $cameraId Focal Length: $focalLength\n")
                    Log.e("focalLength","Camera $cameraId Sensor Size: $sensorSize\n")


                }
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e("facestatus","This is it ${faceStatus.name}")
    }

    private fun updateTVFaceWidth(face: Face) {
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

        if(distanceCurrent < distanceMinimum) {
            distanceMinimum = distanceCurrent
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_STT = 1

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }


    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }

}