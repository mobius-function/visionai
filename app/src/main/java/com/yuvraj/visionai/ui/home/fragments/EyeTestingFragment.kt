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
    private  var textSize: Float = 1.0f
    private  var h_bar: Float = 1.0f
    private var d_base:Float = 350.0f

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

        Log.e("EyeTesting Debug","The initial text size in MM is: $textSize mm")
        // Display Initial text
//        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
//            getResources().getDisplayMetrics());
        val r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, textSize,
            getResources().getDisplayMetrics())
        Log.e("EyeTesting Debug","The initial text size in pixels is: $r px")
        displayRandomText(textSize)

//        u_m0 = 75.0f/distance
        d_base = 350.0f

        // TODO: update distance here
        // distance = min_distance at which user read

        h_bar = textSize * (d_base/(distance*10))
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

    private fun onCheck(flag : Boolean) {

        reading += 1

        if(flag) {
            score += 1
//            u_m0 /= 2
            textSize = h_bar/2
            h_bar = textSize * (d_base/(distance*10))
            Toast.makeText(requireActivity(), "Correct", Toast.LENGTH_SHORT).show()
        } else {
            u_m0 *= 2
            textSize = h_bar * 1.5f
            h_bar = textSize * (d_base/(distance*10))
            Toast.makeText(requireActivity(), "Incorrect", Toast.LENGTH_SHORT).show()
        }

        if(reading <= 6 || h_bar < 0.25f) {
            textSize = DistanceHelper.cmToPixels(u_m0,requireActivity()).toFloat()
            displayRandomText(DistanceHelper.pixelsToDp(textSize.roundToInt()))
        } else {

            val deno : Float = h_bar / 0.50905935f * 20
            Toast.makeText(requireActivity(), "Your score is $score and deno is: $deno", Toast.LENGTH_SHORT).show()
//            textToSpeechEngine.speak("Your score is $score", TextToSpeech.QUEUE_FLUSH, null, "")

//            var x = textSize * 8 / 0.145
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