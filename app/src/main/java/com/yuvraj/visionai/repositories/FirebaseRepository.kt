package com.yuvraj.visionai.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yuvraj.visionai.analytics.HyperopiaTestResultModel
import com.yuvraj.visionai.analytics.MyopiaTestResultModel
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences
import javax.inject.Inject

class FirebaseRepository  @Inject constructor(
    private val db: FirebaseFirestore
) {

    // private val db = Firebase.firestore

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
                Log.d("DebugUserPreferences", document.toObject(UserPreferences::class.java).toString())
            }
            .addOnFailureListener {
                val nullUserPreferences = UserPreferences()
                callback(nullUserPreferences)
                Log.d("DebugUserPreferences", nullUserPreferences.toString() + " " + it.toString())
            }
    }

    fun saveEyeTest(userId: String, eyeTest: EyeTestResult) {
        val eyeTestDoc = mapOf(
            "id" to eyeTest.id,
            "plusPowerRightEye" to eyeTest.plusPowerRightEye,
            "plusPowerLeftEye" to eyeTest.plusPowerLeftEye,
            "minusPowerRightEye" to eyeTest.minusPowerRightEye,
            "minusPowerLeftEye" to eyeTest.minusPowerLeftEye,
            "astigmatismResult" to eyeTest.astigmatismResult,
            "dryLeftEyeResult" to eyeTest.dryLeftEyeResult,
            "dryRightEyeResult" to eyeTest.dryRightEyeResult,
            "jaundiceResult" to eyeTest.jaundiceResult
        )
        // db.collection("users").document(userId).collection("eyeTests").add(eyeTestDoc)
        db.collection("users").document(userId).collection("eyeTests").document(eyeTestDoc["id"].toString()).set(eyeTestDoc)
    }

    // <------------------------------A N A L Y T I C S------------------------------------->
    fun saveHyperopiaTestResult(userId: String, testId: String, testResults: List<HyperopiaTestResultModel>) {
        db.collection("users")
            .document(userId).collection("eyeTests")
            .document(testId).collection("analytics")
            .document("hyperopiaTestResults").set(testResults)
    }

    fun saveMyopiaTestResult(userId: String, testId: String, testResults: List<MyopiaTestResultModel>) {
        db.collection("users")
            .document(userId).collection("eyeTests")
            .document(testId).collection("analytics")
            .document("myopiaTestResults").set(testResults)
    }
    // <--------------------------------------X-------------------------------------------->

    fun getEyeTests(userId: String, callback: (List<EyeTestResult>?) -> Unit) {
        Log.d("DebugEyeTests", "Getting Eye Tests (Repository)")
        db.collection("users").document(userId).collection("eyeTests").get()
            .addOnSuccessListener { documents ->
                val eyeTests = documents.mapNotNull { it.toObject(EyeTestResult::class.java) }
                Log.d("DebugEyeTests", eyeTests.toString())
                callback(eyeTests)
            }
            .addOnFailureListener { e ->
                Log.d("DebugEyeTests", "Failed: ${e.message}")
                callback(null)
            }
    }

    fun getEyeTestsQuery(userId: String): Query {
        return db.collection("users").document(userId).collection("eyeTests").orderBy("id")
    }

    fun getEyeTests(userId: String): Query {
        Log.d("DebugEyeTests", "Getting Eye Tests (Repository)")
        return db.collection("users").document(userId).collection("eyeTests")
    }
}
