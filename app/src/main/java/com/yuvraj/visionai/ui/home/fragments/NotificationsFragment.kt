package com.yuvraj.visionai.ui.home.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.graphics.toColorInt
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeNotificationsBinding

class NotificationsFragment : Fragment(R.layout.fragment_home_notifications) {

    private var _binding: FragmentHomeNotificationsBinding? = null
    private val binding get() = _binding!!

    // listener which is triggered when the
    // time is picked from the time picker dialog
    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> // logic to properly handle
            // the picked timings by user
            val formattedTime: String = when {
                hourOfDay == 0 -> {
                    if (minute < 10) {
                        "${hourOfDay + 12} : 0${minute} am"
                    } else {
                        "${hourOfDay + 12} : $minute am"
                    }
                }

                hourOfDay > 12 -> {
                    if (minute < 10) {
                        "${hourOfDay - 12} : 0$minute pm"
                    } else {
                        "${hourOfDay - 12} : $minute pm"
                    }
                }

                hourOfDay == 12 -> {
                    if (minute < 10) {
                        "$hourOfDay : 0$minute pm"
                    } else {
                        "$hourOfDay : $minute pm"
                    }
                }

                else -> {
                    if (minute < 10) {
                        "$hourOfDay : $minute am"
                    } else {
                        "$hourOfDay : $minute am"
                    }
                }
            }

            binding.tvTimeRegularReminder.text = formattedTime
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
        _binding = FragmentHomeNotificationsBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {
            tvTimeRegularReminder.setOnClickListener {
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    timePickerDialogListener,
                    12,
                    10,
                    false
                )
                timePickerDialog.show()
            }

            tbRegularReminder.setOnCheckedChangeListener() { _, isChecked ->
                if (isChecked) {
                    tvTimeRegularReminder.isClickable = true
                    tvTimeRegularReminder.setTextColor("#FFFFFF".toColorInt())
                } else {
                    tvTimeRegularReminder.isClickable = false
                    tvTimeRegularReminder.setTextColor("#898989".toColorInt())
                }
            }
        }
    }
}