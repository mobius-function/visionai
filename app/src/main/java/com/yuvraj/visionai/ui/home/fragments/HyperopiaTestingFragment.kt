package com.yuvraj.visionai.ui.home.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeEyeTestingBinding
import com.yuvraj.visionai.databinding.FragmentHomeHyperopiaTestingBinding
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.service.faceDetection.FaceStatus
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [HyperopiaTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class HyperopiaTestingFragment : Fragment(R.layout.fragment_home_hyperopia_testing) {

    private var _binding: FragmentHomeHyperopiaTestingBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager

    private val focalLengthFound : Double = 50.0
    private val realFaceWidth : Double = 14.0

    private var distanceCurrent : Float = 0.0f
    private var distanceMaximum : Float = 350.0f
    private var baseDistance:Float = 350.0f

//    private var u_m0 : Float = 0.0f

    private  var textSize: Float = 2.0f
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
        _binding = FragmentHomeHyperopiaTestingBinding.bind(view)

        Log.e("EyeTesting Debug","The initial text size in MM is: $textSize mm")

        // Display Initial text
//        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
//            getResources().getDisplayMetrics());
        val r = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM, textSize,
            getResources().getDisplayMetrics())
        Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
        displayRandomText(textSize)

//        u_m0 = 75.0f/distance
        baseDistance = 350.0f

        // TODO: update distance here
        // distance = min_distance at which user read

        relativeTextSize = textSize * (baseDistance/distanceMaximum)


//        binding.tvCurrentDistance.text = distanceMinimum.toString()
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

        relativeTextSize = textSize * (baseDistance/distanceMaximum)

        if(correctResult) {
            score += 1
//            u_m0 /= 2

            lastCorrect = relativeTextSize
            if(lastIncorrect == null) {
                textSize = relativeTextSize*2
            } else {
                textSize = (relativeTextSize + lastIncorrect!!)/2
            }

            Toast.makeText(requireActivity(), "Correct", Toast.LENGTH_SHORT).show()
        }

        else {
//            u_m0 *= 2

            lastIncorrect = relativeTextSize
            if(lastCorrect == null) {
                textSize = relativeTextSize / 2
            } else {
                textSize = (relativeTextSize + lastCorrect!!)/2
            }

//            textSize = relativeTextSize * 1.5f
//            relativeTextSize = textSize * (baseDistance/(distance*10))
            Toast.makeText(requireActivity(), "Incorrect", Toast.LENGTH_SHORT).show()
        }

        if(reading <= 6 && textSize > 0.25f) {
//            textSize = DistanceHelper.cmToPixels(u_m0,requireActivity()).toFloat()
            displayRandomText(textSize)
            Log.e("EyeTesting Debug","The presented text size in MM is: $textSize mm")
        } else {
            var diapter : Double? = null
            if(lastIncorrect == null) {
                Toast.makeText(requireActivity(), "You don't have + power", Toast.LENGTH_SHORT).show()
            } else {
                var distance : Double = (baseDistance / (0.50905435) * lastIncorrect!!)
                diapter = (1000/(90-15)) - (1000/(distance - 15))
            }
            binding.tvRandomText.setTextSize(TypedValue.COMPLEX_UNIT_MM, 10.0f)
            binding.tvRandomText.text = "$diapter"
            Toast.makeText(requireActivity(), "Your score is $score and deno is: $diapter", Toast.LENGTH_SHORT).show()
//            textToSpeechEngine.speak("Your score is $score", TextToSpeech.QUEUE_FLUSH, null, "")

//            var x = textSize * 8 / 0.145
        }

        distanceMaximum = distanceCurrent
        binding.tvMaximumDistance.text = "Minimum Distance: ${distanceMaximum}"

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

        binding.tvCurrentDistance.text = "Current Distance: ${distance*100}"
        distanceCurrent = distance.toFloat()*100.0f

        if(distanceCurrent > distanceMaximum) {
            distanceMaximum = distanceCurrent
            binding.tvMaximumDistance.text = distanceMaximum.toString()
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