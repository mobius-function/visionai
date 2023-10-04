package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingSignupBinding
import com.yuvraj.visionai.utils.helpers.Validations


/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment(R.layout.fragment_on_boarding_signup) {
    private var _binding: FragmentOnBoardingSignupBinding? = null
    private val binding get() = _binding!!

//    lateinit var mGoogleSignInClient: GoogleSignInClient
//    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
//        textWatcher()
//        binding.btn.btnStart.isEnabled = false
        binding.btnCreateAccount.isEnabled = true
//        showSoftKeyboard(requireActivity(),binding.etName)
        clickableViews(binding.etName,binding.etEmail,binding.etPassword,binding.etConfirmPassword)

//        FirebaseApp.initializeApp(this.requireActivity())

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
//        firebaseAuth = FirebaseAuth.getInstance()
    }


//    private fun signInGoogle() {
//        val signInIntent: Intent = mGoogleSignInClient.signInIntent
//        startActivity(signInIntent)
//    }


    private fun clickableViews(Name : EditText, Email : EditText, Password : EditText, ConfirmPassword : EditText) {
        binding.apply {
//            btn.btnStart.setOnClickListener {
//                findNavController().navigate(R.id.action_onBoardingAboutFrag_to_onBoardingCategoriesFragment)
//            }
            btnCreateAccount.setOnClickListener {

                var flag: Boolean = true

                if(!Validations.validateFirstName(Name.text.toString())){
                    Name.error = "Enter a Valid Name"
                    flag = false
                }


                if(!Validations.validateEmail(Email.text.toString())){
                    Email.error = "Enter a Valid Email"
                    flag = false
                }

                if(!Validations.validatePassword(Password.text.toString())){
                    Password.error = "Password must be atleast of 8 characters"
                    flag = false
                }

                if(!Validations.validateConfirmPassword(Password.text.toString(),ConfirmPassword.text.toString())){
                    ConfirmPassword.error = "Password and confirm password should match"
                    flag = false
                }

                if(flag) {
                    findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
                }
            }

//            googleButton.setOnClickListener() {view : View? ->
//                signInGoogle()
//                findNavController().navigate(R.id.action_onBoardingSignUpFragment_to_onBoardingAboutFrag)
//            }

            tvLoginHere.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            }

            tvSkipForNow.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
            }

            back.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingSignupBinding.bind(view)
//        ConstantsFunctions.setStatusBarTransparent(requireActivity(), false)

    }
}