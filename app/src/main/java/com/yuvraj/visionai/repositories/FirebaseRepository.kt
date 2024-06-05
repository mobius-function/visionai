package com.yuvraj.visionai.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveApiKey(apiKey: String) {
        val apiKeyDoc = mapOf("apiKey" to apiKey)
        db.collection("config").document("apiKey").set(apiKeyDoc)
    }

    fun getApiKey(callback: (String?) -> Unit) {
        db.collection("config").document("apiKey").get()
            .addOnSuccessListener { document ->
                callback(document.getString("apiKey"))
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun saveUserPreferences(userId: String, preferences: UserPreferences) {
        db.collection("users").document(userId).set(preferences)
    }

    fun getUserPreferences(userId: String, callback: (UserPreferences?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                callback(document.toObject(UserPreferences::class.java))
            }
            .addOnFailureListener {
                callback(null)
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
