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
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.service.faceDetection.FaceStatus
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

    private val focalLengthFound : Double = 50.0
    private val realFaceWidth : Double = 14.0

    private var distanceMinimum : Float = 350.0f
    private var baseDistance:Float = 350.0f

//    private var u_m0 : Float = 0.0f

    private  var textSize: Float = 1.0f
    private  var relativeTextSize: Float = 1.0f


    private var reading : Int = 0
    private var score : Int = 0

    private var lastCorrect: Float? = null
    private var lastIncorrect: Float? = null

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(requireActivity()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.US
            }
        }

//        TextToSpeech(requireActivity(),
//            TextToSpeech.OnInitListener { status ->
//                if (status == TextToSpeech.SUCCESS) {
//                    textToSpeechEngine.language = Locale.US
//                }
//            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        createCameraManager()
        checkForPermission()
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeEyeTestingBinding.bind(view)

        Log.e("EyeTesting Debug","The initial text size in MM is: $textSize mm")
        // Display Initial text
//        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
//            getResources().getDisplayMetrics());
        val r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
            getResources().getDisplayMetrics())
        Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
        displayRandomText(textSize)

//        u_m0 = 75.0f/distance
        baseDistance = 350.0f

        // TODO: update distance here
        // distance = min_distance at which user read

        relativeTextSize = textSize * (baseDistance/distanceMinimum)
    }

    private fun clickableViews() {

        binding.apply {
            btnSpeech.setOnClickListener {
                val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                sttIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")

                try {
                    startActivityForResult(sttIntent, REQUEST_CODE_STT)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireActivity(),
                        "Your device does not support Speech To Text.",
                        Toast.LENGTH_LONG).show()
                }
            }

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
//            Log.e("EyeTesting Debug","The initial text size in DP is: $textSizeDisplay dp")
            tvRandomText.text = textDisplay
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]

                        binding.textViewSpeechToText.text = recognizedText.toString()

                        onCheck(binding.textViewSpeechToText.text.toString().lowercase() ==
                                binding.tvRandomText.text.toString().lowercase())
                    }
                }
            }
        }
    }

    private fun onCheck(correctResult : Boolean) {

        reading += 1

        if(correctResult) {
            score += 1
//            u_m0 /= 2

            lastCorrect = relativeTextSize
            if(lastIncorrect == null) {
                textSize = relativeTextSize/2
            } else {
                textSize = (relativeTextSize + lastIncorrect!!)/2
            }

            relativeTextSize = textSize * (baseDistance/(distanceMinimum))
            Toast.makeText(requireActivity(), "Correct", Toast.LENGTH_SHORT).show()
        } else {
//            u_m0 *= 2

            lastIncorrect = relativeTextSize
            if(lastCorrect == null) {
                textSize = relativeTextSize * 2
            } else {
                textSize = (relativeTextSize + lastCorrect!!)/2
            }

//            textSize = relativeTextSize * 1.5f
//            relativeTextSize = textSize * (baseDistance/(distance*10))
            Toast.makeText(requireActivity(), "Incorrect", Toast.LENGTH_SHORT).show()
        }

        if(reading <= 6 && textSize >= 0.5f) {
//            textSize = DistanceHelper.cmToPixels(u_m0,requireActivity()).toFloat()
            displayRandomText(textSize)
            Log.e("EyeTesting Debug","The presented text size in MM is: $textSize mm")
        } else {
            var deno : Double? = null
            if(lastCorrect != null) {
                deno = (lastCorrect!! * 20)/0.50905435
            } else {
                deno = (lastIncorrect!! * 20)/0.50905435
            }
            Toast.makeText(requireActivity(), "Your score is $score and deno is: $deno", Toast.LENGTH_SHORT).show()
//            textToSpeechEngine.speak("Your score is $score", TextToSpeech.QUEUE_FLUSH, null, "")

//            var x = textSize * 8 / 0.145
        }

        distanceMinimum = binding.tvCurrentDistance.text.toString().toFloat() * 10.0f

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
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e("facestatus","This is it ${faceStatus.name}")
//        tvFaceWidth.text
//       when(faceStatus){}
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

        binding.tvCurrentDistance.text = "Face Width: ${distance*10}"

        if(binding.tvCurrentDistance.text.toString().toFloat() * 10.0f < distanceMinimum) {
            distanceMinimum = binding.tvCurrentDistance.text.toString().toFloat() * 10.0f
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