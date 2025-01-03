package com.yuvraj.visionai.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeGeneratedResultBinding
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.clients.AlertDialogBox
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastAstigmatismResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastDryEyeResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastHyperopiaResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setPastMyopiaResults
import com.yuvraj.visionai.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class GeneratedResultFragment : Fragment(R.layout.fragment_home_generated_result) {
    private var _binding: FragmentHomeGeneratedResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

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

        val eyeTestResult : EyeTestResult = viewModel.eyeTestResult.value!!

        val id = eyeTestResult.id

        val myopiaResultsLeftEye = eyeTestResult.minusPowerLeftEye
        val myopiaResultsRightEye = eyeTestResult.minusPowerRightEye

        val hyperopiaResultsLeftEye = eyeTestResult.plusPowerLeftEye
        val hyperopiaResultsRightEye = eyeTestResult.plusPowerRightEye

        val dryLeftEyeResults = eyeTestResult.dryLeftEyeResult
        val dryRightEyeResults = eyeTestResult.dryRightEyeResult

        val astigmatismResult = eyeTestResult.astigmatismResult


        requireActivity().apply {
            setPastMyopiaResults(myopiaResultsLeftEye!!, myopiaResultsRightEye!!)
            setPastHyperopiaResults(hyperopiaResultsLeftEye!!, hyperopiaResultsRightEye!!)
            setPastDryEyeResults(dryLeftEyeResults!!, dryRightEyeResults!!)
            setPastAstigmatismResults(astigmatismResult!!)
        }

        binding.generatedResult.apply {
            text = "Your eye test results are as follows:\n\n" +
                "ID: $id\n" +
                "Astigmatism: $astigmatismResult\n" +
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