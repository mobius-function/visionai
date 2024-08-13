package com.yuvraj.visionai.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingLoginBinding

class LoginFragment : Fragment(R.layout.fragment_on_boarding_login) {

    private var _binding: FragmentOnBoardingLoginBinding? = null
    private val binding get() = _binding!!

    // creating a variable for firebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        clickableViews()
    }


    private fun clickableViews() {
        binding.apply {

            btnLogin.setOnClickListener {
                if(etEmail.text.toString().isEmpty()){
                    tilEmail.error = "Please enter your Email"
                    return@setOnClickListener
                } else {
                    tilEmail.error = null
                }

                if(etPassword.text.toString().isEmpty()){
                    tilPassword.error = "Please enter your Password"
                    return@setOnClickListener
                } else {
                    tilPassword.error = null
                }

                login()
            }

            tvSignUpHere.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingLoginBinding.bind(view)

        auth = FirebaseAuth.getInstance()
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {
                Toast.makeText(requireActivity(), "Successfully Logged In", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)

            } else
                Toast.makeText(requireActivity(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show()
        }
    }
}