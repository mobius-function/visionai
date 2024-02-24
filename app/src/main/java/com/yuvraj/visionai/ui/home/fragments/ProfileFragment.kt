package com.yuvraj.visionai.ui.home.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.bumptech.glide.Glide
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeProfileBinding
import com.yuvraj.visionai.firebase.Authentication
import com.yuvraj.visionai.utils.Constants


class ProfileFragment : Fragment(R.layout.fragment_home_profile) {

    private var _binding: FragmentHomeProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        setupProfile()
        clickableViews()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeProfileBinding.bind(view)
    }

    private fun clickableViews() {
        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }


    private fun setupProfile() {
        val user = Authentication.getSignedInUser()

        binding.apply {
            miniProfileTab.tvName.text = user?.displayName
//            miniProfileTab.ivProfilePicture.setImageURI(user?.photoUrl)
            Glide.with(requireActivity()).load(user?.photoUrl).into(miniProfileTab.ivProfilePicture)
            emailTab.tvEmail.text = user?.email
            val sharedPreferences = requireActivity().getSharedPreferences(
                Constants.USER_DETAILS,
                Context.MODE_PRIVATE
            )
            val phoneNumber = sharedPreferences.getLong(Constants.USER_PHONE, 20)
            infoTab.tvMobile.text = phoneNumber.toString()

        }
    }
}