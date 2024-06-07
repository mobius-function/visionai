package com.yuvraj.visionai.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences
import com.yuvraj.visionai.repositories.EyeTestPagingSource
import com.yuvraj.visionai.repositories.FirebaseRepository
import kotlinx.coroutines.flow.Flow

class UserViewModel : ViewModel() {

    private val userRepository = FirebaseRepository()
    private val pagingConfig = PagingConfig(pageSize = 10)

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

    fun getEyeTestsPagingData(userId: String): Flow<PagingData<EyeTestResult>> {
        val query = userRepository.getEyeTests(userId)
        return Pager(pagingConfig) {
            EyeTestPagingSource(query)
        }.flow.cachedIn(viewModelScope)
    }

    fun saveEyeTest(userId: String, eyeTest: EyeTestResult) {
        userRepository.saveEyeTest(userId, eyeTest)
    }
}
