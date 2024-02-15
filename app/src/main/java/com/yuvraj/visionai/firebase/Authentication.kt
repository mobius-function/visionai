package com.yuvraj.visionai.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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