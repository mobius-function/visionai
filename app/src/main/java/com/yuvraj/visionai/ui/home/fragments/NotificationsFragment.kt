package com.yuvraj.visionai.ui.home.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeNotificationsBinding
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.EYE_TEST_REMINDER
import com.yuvraj.visionai.utils.Constants.REGULAR_REMINDER
import com.yuvraj.visionai.utils.Constants.REGULAR_REMINDER_TIME
import com.yuvraj.visionai.utils.clients.NotificationHelper.setRegularReminderTime

class NotificationsFragment : Fragment(R.layout.fragment_home_notifications) {

    private var _binding: FragmentHomeNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeNotificationsBinding.bind(view)

        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.NOTIFICATION_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        binding.apply {
            tbEyeTestReminder.isChecked =
                sharedPreferences.getBoolean(EYE_TEST_REMINDER, true)
            tbRegularReminder.isChecked =
                sharedPreferences.getBoolean(REGULAR_REMINDER, false)

            tvTimeInHours.text =
                (sharedPreferences.getInt(REGULAR_REMINDER_TIME, 2)).toString()


            if (tbRegularReminder.isChecked) {
                layoutTimeSelector.visibility = View.VISIBLE
            } else {
                layoutTimeSelector.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun clickableViews() {
        binding.apply {
            btnTimeInHoursPlus.setOnClickListener {
                if (tvTimeInHours.text.toString().toInt() <= 23) {
                    tvTimeInHours.text = (tvTimeInHours.text.toString().toInt() + 1).toString()
                    requireActivity().setRegularReminderTime(tvTimeInHours.text.toString().toInt())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Time should be less than 24 hours",
                        Toast.LENGTH_SHORT).show()
                }
            }

            btnTimeInHoursMinus.setOnClickListener {
                if (tvTimeInHours.text.toString().toInt() >= 2) {
                    tvTimeInHours.text = (tvTimeInHours.text.toString().toInt() - 1).toString()
                    requireActivity().setRegularReminderTime(tvTimeInHours.text.toString().toInt())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Time should be more than 2 hours",
                        Toast.LENGTH_SHORT).show()
                }
            }


            tbEyeTestReminder.setOnCheckedChangeListener{_, isChecked ->
                val sharedPreferences = requireActivity().getSharedPreferences(
                    Constants.NOTIFICATION_PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                )

                val sharedPreferencesEditor = sharedPreferences.edit()
                sharedPreferencesEditor.putBoolean(EYE_TEST_REMINDER, isChecked)
                sharedPreferencesEditor.apply()
            }

            tbRegularReminder.setOnCheckedChangeListener { _, isChecked ->
                toggleRegularReminderIntervalVisibility(isChecked)
            }
        }
    }


    private fun toggleRegularReminderIntervalVisibility(isChecked : Boolean) {
        TransitionManager.beginDelayedTransition(binding.layoutTimeSelector, AutoTransition())
        if (binding.tbRegularReminder.isChecked) {
            binding.apply{
                layoutTimeSelector.visibility = View.VISIBLE
                tvTimeInHours.setTextColor("#FFFFFF".toColorInt())
                btnTimeInHoursMinus.backgroundTintList = (ColorStateList.valueOf("#FFFFFF".toColorInt()))
                btnTimeInHoursPlus.backgroundTintList = (ColorStateList.valueOf("#FFFFFF".toColorInt()))
            }
        } else {
            binding.apply {
                layoutTimeSelector.visibility = View.INVISIBLE
                tvTimeInHours.setTextColor("#898989".toColorInt())
                btnTimeInHoursMinus.backgroundTintList = (ColorStateList.valueOf("#898989".toColorInt()))
                btnTimeInHoursPlus.backgroundTintList = (ColorStateList.valueOf("#898989".toColorInt()))
            }
        }

        val sharedPreferences = requireActivity().getSharedPreferences(
            Constants.NOTIFICATION_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(REGULAR_REMINDER, isChecked)
        sharedPreferencesEditor.apply()
    }
}