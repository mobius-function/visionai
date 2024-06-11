package com.yuvraj.visionai.repositories

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences

class FirebaseRepository {

    private val db = Firebase.firestore

    fun getApiKey(callback: (String?) -> Unit) {
        db.collection("config").document("apiKeys").get()
            .addOnSuccessListener { document ->
                callback(document.getString("CHAT_AUTHORIZATION"))
            }
            .addOnFailureListener {
                callback("")
            }
    }

    fun saveUserPreferences(userId: String, preferences: UserPreferences) {
        val preferencesDoc = mapOf(
            "name" to preferences.name,
            "phoneNumber" to preferences.phoneNumber,
            "age" to preferences.age,
            "gender" to preferences.gender,
            "email" to preferences.email
        )

        db.collection("users").document(userId).set(preferencesDoc)
    }

    fun getUserPreferences(userId: String, callback: (UserPreferences?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                callback(document.toObject(UserPreferences::class.java))
            }
            .addOnFailureListener {
                val nullUserPreferences = UserPreferences()
                callback(nullUserPreferences)
            }
    }

    fun saveEyeTest(userId: String, eyeTest: EyeTestResult) {
        db.collection("users").document(userId).collection("eyeTests").add(eyeTest)
    }

    fun getEyeTests(userId: String, callback: (List<EyeTestResult>?) -> Unit) {
        db.collection("users").document(userId).collection("eyeTests").get()
            .addOnSuccessListener { documents ->
                val eyeTests = documents.mapNotNull { it.toObject(EyeTestResult::class.java) }
                callback(eyeTests)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getEyeTests(userId: String): Query {
        return db.collection("users").document(userId).collection("eyeTests")
    }
}
