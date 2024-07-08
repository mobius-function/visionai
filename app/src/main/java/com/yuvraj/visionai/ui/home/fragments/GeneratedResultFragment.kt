package com.yuvraj.visionai.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeGeneratedResultBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import java.util.Calendar

class GeneratedResultFragment : Fragment(R.layout.fragment_home_generated_result) {
    private var _binding: FragmentHomeGeneratedResultBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentHomeGeneratedResultBinding.bind(view)

        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        // get current data in format (DD/MM/YYYY) and time in format (HH:MM:SS) as id
        val id = Calendar.getInstance().time.toString()

        val totalTimeSpent : Double = sharedPreferences.getLong(Constants.TOTAL_TIME_SPENT_TESTING, 0).toDouble() / 60
        val totalLeftEyePartialBlinkCounter = sharedPreferences.getInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
        val totalRightEyePartialBlinkCounter = sharedPreferences.getInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)

        val myopiaResultsLeftEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_LEFT_EYE, 0.0f)
        val myopiaResultsRightEye = sharedPreferences.getFloat(Constants.MYOPIA_RESULTS_RIGHT_EYE, 0.0f)

        val hyperopiaResultsLeftEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_LEFT_EYE, 0.0f)
        val hyperopiaResultsRightEye = sharedPreferences.getFloat(Constants.HYPEROPIA_RESULTS_RIGHT_EYE, 0.0f)

        val dryLeftEyeResults = totalLeftEyePartialBlinkCounter/totalTimeSpent > 10
        val dryRightEyeResults = totalRightEyePartialBlinkCounter/totalTimeSpent > 10

        binding.generatedResult.apply {
            text = "Your eye test results are as follows:\n\n" +
                "ID: $id\n" +
                "Astigmatism: ${sharedPreferences.getFloat(Constants.ASTIGMATISM_RESULTS, 0.0f)}\n" +
                "Dry Left Eye: $dryLeftEyeResults\n" +
                "Dry Right Eye: $dryRightEyeResults\n" +
//                "Jaundice: ${sharedPreferences.getBoolean(Constants.JAUNDICE_RESULTS, false)}\n" +
                "Plus Power Left Eye: $hyperopiaResultsLeftEye\n" +
                "Plus Power Right Eye: $hyperopiaResultsRightEye\n" +
                "Minus Power Left Eye: $myopiaResultsLeftEye\n" +
                "Minus Power Right Eye: $myopiaResultsRightEye"
        }
    }

    private fun clickableViews() {
        binding.apply {
            btnDone.setOnClickListener {
                // Remove past fragments from backstack
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    // override on back pressed function
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}