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
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import com.yuvraj.visionai.utils.helpers.DistanceHelper
import com.yuvraj.visionai.utils.PowerAlgorithm
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [EyeTestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EyeTestingFragment : Fragment(R.layout.fragment_home_eye_testing) {

    private var _binding: FragmentHomeEyeTestingBinding? = null
    private val binding get() = _binding!!

    private var distance : Float = 75.0f
    private var u_m0 : Float = 0.0f
    private  var textSize: Float = 0.0f

    private var reading : Int = 0
    private var score : Int = 0


    companion object {
        private const val REQUEST_CODE_STT = 1
    }

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
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeEyeTestingBinding.bind(view)

        val textSizeInCM : Float = PowerAlgorithm.generateInitialPowerText().toFloat()
//        val textSizeInCM : Float = 3.0f

        Log.e("EyeTesting Debug","The initial text size in CM is: $textSizeInCM cm")
        // Display Initial text
        textSize = DistanceHelper.cmToPixels(textSizeInCM, requireActivity()).toFloat()
        Log.e("EyeTesting Debug","The initial text size in pixels is: $textSize px")
        displayRandomText(DistanceHelper.pixelsToDp(textSize.roundToInt()))

        u_m0 = 75.0f/distance
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
        val textDisplay : String = (((0..25).random() + 65).toChar()).toString() +
                                    (((0..25).random() + 65).toChar()).toString()

        binding.apply {
            tvRandomText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeDisplay)
            Log.e("EyeTesting Debug","The initial text size in DP is: $textSizeDisplay dp")
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

    private fun onCheck(flag : Boolean) {

        reading += 1

        if(flag) {
            score += 1
            u_m0 /= 2
            Toast.makeText(requireActivity(), "Correct", Toast.LENGTH_SHORT).show()
        } else {
            u_m0 *= 2
            Toast.makeText(requireActivity(), "Incorrect", Toast.LENGTH_SHORT).show()
        }

        if(reading < 8) {
            textSize = DistanceHelper.cmToPixels(u_m0,requireActivity()).toFloat()
            displayRandomText(DistanceHelper.pixelsToDp(textSize.roundToInt()))
        } else {
            Toast.makeText(requireActivity(), "Your score is $score", Toast.LENGTH_SHORT).show()
//            textToSpeechEngine.speak("Your score is $score", TextToSpeech.QUEUE_FLUSH, null, "")

            var x = textSize * 8 / 0.145
        }

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