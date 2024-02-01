package com.yuvraj.visionai.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yuvraj.visionai.R
import com.yuvraj.visionai.ui.onBoarding.MainActivity
import com.yuvraj.visionai.ui.onBoarding.fragments.SplashFragmentDirections
import com.yuvraj.visionai.utils.Constants

class Authentication {
    companion object {

        fun getSignedInUser(): FirebaseUser? {
            return FirebaseAuth.getInstance().currentUser
        }

        fun signOutUser() {
            Firebase.auth.signOut()
        }
    }
}