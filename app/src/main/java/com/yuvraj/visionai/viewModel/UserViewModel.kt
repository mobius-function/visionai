package com.yuvraj.visionai.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yuvraj.visionai.analytics.HyperopiaTestResultModel
import com.yuvraj.visionai.analytics.MyopiaTestResultModel
import com.yuvraj.visionai.firebase.Authentication
import com.yuvraj.visionai.model.EyeTestResult
import com.yuvraj.visionai.model.UserPreferences
import com.yuvraj.visionai.repositories.EyeTestPagingSource
import com.yuvraj.visionai.repositories.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: FirebaseRepository
) : ViewModel() {

    private val pagingConfig = PagingConfig(pageSize = 10)

//    private val userRepository = FirebaseRepository()

    val apiKey: MutableLiveData<String?> = MutableLiveData()
    val userPreferences: MutableLiveData<UserPreferences?> = MutableLiveData()
    val eyeTests: MutableLiveData<List<EyeTestResult>?> = MutableLiveData()

    // TODO: Add live data for analytics!
//    val myopiaTestResultModelList: MutableLiveData<List<MyopiaTestResultModel>> = MutableLiveData()
//    val hyperopiaTestResultModelList: MutableLiveData<List<HyperopiaTestResultModel>> = MutableLiveData()

    private val userId = Authentication.getSignedInUser()?.uid ?: "GUEST_CUSTOMER_ID"

    fun loadApiKey() {
        userRepository.getApiKey { key ->
            apiKey.value = key
        }
    }

    fun loadUserPreferences() {
        userRepository.getUserPreferences(userId) {
            if(it != null){
                userPreferences.value = it
            }
        }
    }

    fun saveUserPreferences(preferences: UserPreferences) {
        userRepository.saveUserPreferences(userId, preferences)
        userPreferences.value = preferences
    }

//    fun loadEyeTests() {
//        userRepository.getEyeTests(userId) { tests ->
//            eyeTests.value = tests
//        }
//    }

    fun getEyeTestsPagingData(): Flow<PagingData<EyeTestResult>> {
        Log.d("DebugEyeTests","Getting Eye Tests (View Model)")
        val query = userRepository.getEyeTests(userId)
        return Pager(pagingConfig) {
            Log.d("DebugEyeTests","Paging Eye Tests (View Model)")
            EyeTestPagingSource(query)
        }.flow.cachedIn(viewModelScope)
    }

    fun saveEyeTest(eyeTest: EyeTestResult) {
        userRepository.saveEyeTest(userId, eyeTest)
    }

    fun saveTestDetails() {

    }
}
