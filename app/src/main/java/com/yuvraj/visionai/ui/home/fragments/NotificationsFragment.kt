package com.yuvraj.visionai.ui.home.fragments

import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.graphics.toColorInt
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeNotificationsBinding

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
    }

    private fun clickableViews() {
        binding.apply {

            tbRegularReminder.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvTimeInHours.setTextColor("#FFFFFF".toColorInt())
                    btnTimeInHoursMinus.backgroundTintList = (ColorStateList.valueOf("#FFFFFF".toColorInt()))
                    btnTimeInHoursPlus.backgroundTintList = (ColorStateList.valueOf("#FFFFFF".toColorInt()))
                } else {
                    tvTimeInHours.setTextColor("#898989".toColorInt())
                    btnTimeInHoursMinus.backgroundTintList = (ColorStateList.valueOf("#898989".toColorInt()))
                    btnTimeInHoursPlus.backgroundTintList = (ColorStateList.valueOf("#898989".toColorInt()))
                }
            }
        }
    }
}