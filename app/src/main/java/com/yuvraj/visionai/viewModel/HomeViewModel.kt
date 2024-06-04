package com.yuvraj.visionai.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences
import com.yuvraj.visionai.repositories.FirebaseRepository

class HomeViewModel : ViewModel() {

    private val userRepository = FirebaseRepository()

    val apiKey: MutableLiveData<String?> = MutableLiveData()
    val userPreferences: MutableLiveData<UserPreferences?> = MutableLiveData()
    val eyeTests: MutableLiveData<List<EyeTestResult>?> = MutableLiveData()

    fun loadApiKey() {
        userRepository.getApiKey { key ->
            apiKey.value = key
        }
    }

    fun saveApiKey(key: String) {
        userRepository.saveApiKey(key)
        apiKey.value = key
    }

    fun loadUserPreferences(userId: String) {
        userRepository.getUserPreferences(userId) { preferences ->
            userPreferences.value = preferences
        }
    }

    fun saveUserPreferences(userId: String, preferences: UserPreferences) {
        userRepository.saveUserPreferences(userId, preferences)
        userPreferences.value = preferences
    }

    fun loadEyeTests(userId: String) {
        userRepository.getEyeTests(userId) { tests ->
            eyeTests.value = tests
        }
    }

    fun saveEyeTest(userId: String, eyeTest: EyeTestResult) {
        userRepository.saveEyeTest(userId, eyeTest)
    }
}
