package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeNotificationsBinding

class NotificationsFragment : Fragment(R.layout.fragment_home_notifications) {

    private var _binding: FragmentHomeNotificationsBinding? = null
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
        _binding = FragmentHomeNotificationsBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {
        }
    }
}