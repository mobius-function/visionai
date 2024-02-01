package com.yuvraj.visionai.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingLoginBinding
import com.yuvraj.visionai.utils.helpers.Validations

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(R.layout.fragment_on_boarding_login) {

    private var _binding: FragmentOnBoardingLoginBinding? = null
    private val binding get() = _binding!!

    // creating a variable for firebaseAuth instance
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
//        textWatcher()
//        binding.btn.btnStart.isEnabled = false
        binding.btnLogin.isEnabled = true
//        showSoftKeyboard(requireActivity(),binding.etName)
        clickableViews(binding.etEmail,binding.etPassword)

        auth = FirebaseAuth.getInstance()
    }

//    private fun textWatcher() {
//        binding.etName.addTextWatcher()
//    }


    private fun clickableViews(Email : EditText, Password : EditText) {
        binding.apply {

            btnLogin.setOnClickListener {
                if(etEmail.text.toString().isEmpty()){
                    etEmail.error = "Please enter your Email"
                    return@setOnClickListener
                }

                if(etPassword.text.toString().isEmpty()){
                    etPassword.error = "Please enter your Password"
                    return@setOnClickListener
                }

                login()
            }

            tvSignUpHere.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }

            tvSkipForNow.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
            }

            back.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingLoginBinding.bind(view)
//        ConstantsFunctions.setStatusBarTransparent(requireActivity(), false)

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