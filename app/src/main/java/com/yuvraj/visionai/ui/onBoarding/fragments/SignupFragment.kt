package com.yuvraj.visionai.ui.onBoarding.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentOnBoardingSignupBinding
import com.yuvraj.visionai.firebase.Authentication.Companion.getSignedInUser
import com.yuvraj.visionai.utils.helpers.Validations


class SignupFragment : Fragment(R.layout.fragment_on_boarding_signup) {
    private var _binding: FragmentOnBoardingSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var registerAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForSignedInUser()

        initViews(view)
        binding.btnCreateAccount.isEnabled = true
        clickableViews()

        FirebaseApp.initializeApp(this.requireActivity())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        firebaseAuth = FirebaseAuth.getInstance()
        registerAuth = Firebase.auth
    }


    private fun checkForSignedInUser() {
        if (getSignedInUser() != null) {
            findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
        }
    }


    private fun clickableViews() {
        binding.apply {

            btnCreateAccount.setOnClickListener {

                if(!Validations.validateFirstName(etName.text.toString())){
                    etName.error = "Enter a Valid Name"
                    return@setOnClickListener
                }

                if(!Validations.validateEmail(etEmail.text.toString())){
                    etEmail.error = "Enter a Valid Email"
                    return@setOnClickListener
                }

                if(!Validations.validatePassword(etPassword.text.toString())){
                    etPassword.error = "Password must be atleast of 8 characters"
                    return@setOnClickListener
                }

                if(!Validations.validateConfirmPassword(etPassword.text.toString(),etConfirmPassword.text.toString())){
                    etConfirmPassword.error = "Password and confirm password should match"
                    return@setOnClickListener
                }

                progressBar.visibility = View.VISIBLE
                signUpUser()
            }

            btnGoogleSignIn.setOnClickListener() {
                progressBar.visibility = View.VISIBLE
                signInGoogle()
                Toast.makeText(requireActivity(), "Logging In", Toast.LENGTH_SHORT).show()
            }

            tvLoginHere.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            }

            tvSkipForNow.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initViews(view: View) {
        _binding = FragmentOnBoardingSignupBinding.bind(view)
    }


    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        getResult.launch(signInIntent)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleResult(task)
            }
        }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
            }
        }
    }

    private fun signUpUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // check pass
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(requireActivity(), "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(requireActivity(), "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        registerAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(requireActivity()) {
            if (it.isSuccessful) {

                val user = FirebaseAuth.getInstance().currentUser
                if(user != null) {
                    // val profileUpdates = userProfileChangeRequest {
                    //    displayName = binding.etName.text.toString()
                    //    photoUri = null
                    // }

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.etName.text.toString())
                        .setPhotoUri(null)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireActivity(), "Successfully Singed Up as ${user.displayName}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                findNavController().navigate(R.id.action_signupFragment_to_detailsFragment)
            } else {
                Toast.makeText(requireActivity(), "Singed Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.GONE
    }
}