package com.yuvraj.visionai.ui.onBoarding.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingSignupBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.helpers.Validations


/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment(R.layout.fragment_on_boarding_signup) {
    private var _binding: FragmentOnBoardingSignupBinding? = null
    private val binding get() = _binding!!

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForSignedInUser()

        initViews(view)
//        textWatcher()
//        binding.btn.btnStart.isEnabled = false
        binding.btnCreateAccount.isEnabled = true
//        showSoftKeyboard(requireActivity(),binding.etName)
        clickableViews(binding.etName,binding.etEmail,binding.etPassword,binding.etConfirmPassword)

        FirebaseApp.initializeApp(this.requireActivity())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
//
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        firebaseAuth = FirebaseAuth.getInstance()
    }


    private fun checkForSignedInUser() {
        if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
            startActivity(
                Intent(
                    requireActivity(), MainActivity::class.java
                )
            )
            requireActivity().finish()
        }
    }


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

            btnGoogleSignIn.setOnClickListener() {view : View? ->
                signInGoogle()
                Toast.makeText(requireActivity(), "Logging In", Toast.LENGTH_SHORT).show()
                // findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
            }

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


    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("SignupFragment Debug","The request code is: $requestCode")
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                SavedPreferences.setEmail(requireActivity(), account.email.toString())
//                SavedPreferences.setUsername(requireActivity(), account.displayName.toString())
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkForSignedInUser()
    }
}