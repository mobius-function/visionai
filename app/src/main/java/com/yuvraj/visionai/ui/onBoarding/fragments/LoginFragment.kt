package com.yuvraj.visionai.ui.onBoarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
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
    }

//    private fun textWatcher() {
//        binding.etName.addTextWatcher()
//    }


    private fun clickableViews(Email : EditText, Password : EditText) {
        binding.apply {
//            btn.btnStart.setOnClickListener {
//                findNavController().navigate(R.id.action_onBoardingAboutFrag_to_onBoardingCategoriesFragment)
//            }
            btnLogin.setOnClickListener {

                var flag: Boolean = true

                if(!Validations.validateEmail(Email.text.toString())){
                    Email.error = "Enter a Valid Email"
                    flag = false
                }

                if(!Validations.validatePassword(Password.text.toString())){
                    Password.error = "Password must be atleast of 8 characters"
                    flag = false
                }

                if(flag) {
                    findNavController().navigate(R.id.action_loginFragment_to_detailsFragment)
                }
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
}