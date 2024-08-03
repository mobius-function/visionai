package com.yuvraj.visionai.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
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

    // private val userRepository = FirebaseRepository()

    private val pagingConfig = PagingConfig(pageSize = 10)

    private val userId = Authentication.getSignedInUser()?.uid ?: "GUEST_CUSTOMER_ID"
    val apiKey: MutableLiveData<String?> = MutableLiveData()
    val userPreferences: MutableLiveData<UserPreferences?> = MutableLiveData()


    // MutableLiveData to hold the EyeTestResult
    private val _eyeTestResult = MutableLiveData<EyeTestResult>().apply {
        value = EyeTestResult()
    }

    // LiveData to expose the EyeTestResult
    val eyeTestResult: LiveData<EyeTestResult> = _eyeTestResult

    // Method to update plusPower in right eye
    fun updatePlusPowerRightEye(value: Float) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(plusPowerRightEye = value)
    }

    // Method to update plusPower in left eye
    fun updatePlusPowerLeftEye(value: Float) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(plusPowerLeftEye = value)
    }

    // Method to update minusPower in right eye
    fun updateMinusPowerRightEye(value: Float) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(minusPowerRightEye = value)
    }

    // Method to update minusPower in left eye
    fun updateMinusPowerLeftEye(value: Float) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(minusPowerLeftEye = value)
    }

    // Method to update astigmatism result
    fun updateAstigmatismResult(value: Boolean) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(astigmatismResult = value)
    }

    // Method to update dry left eye result
    fun updateDryLeftEyeResult(value: Boolean) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(dryLeftEyeResult = value)
    }

    // Method to update dry right eye result
    fun updateDryRightEyeResult(value: Boolean) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(dryRightEyeResult = value)
    }

    // Method to update jaundice result
    fun updateJaundiceResult(value: Boolean) {
        _eyeTestResult.value = _eyeTestResult.value?.copy(jaundiceResult = value)
    }


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

    fun getEyeTestsPagingData(): Flow<PagingData<EyeTestResult>> {
        Log.d("DebugEyeTests","Getting Eye Tests (View Model)")
        val query = userRepository.getEyeTests(userId)
        return Pager(pagingConfig) {
            Log.d("DebugEyeTests","Paging Eye Tests (View Model)")
            EyeTestPagingSource(query)
        }.flow.cachedIn(viewModelScope)
    }

    fun saveEyeTest() {
        Log.d("DebugEyeTests", "Saved EyeTest: ${eyeTestResult.value}")
        eyeTestResult.value?.let { userRepository.saveEyeTest(userId, it) }
    }

    // <------------------------------A N A L Y T I C S------------------------------------->
    // TODO: Add live data for analytics!
    // LiveData to hold the list of test results
    private val _hyperopiaTestResultModel = MutableLiveData<List<HyperopiaTestResultModel>>().apply {
        value = mutableListOf()
    }
    val hyperopiaTestResultModel: LiveData<List<HyperopiaTestResultModel>> = _hyperopiaTestResultModel

    private val _myopiaTestResultModel = MutableLiveData<List<MyopiaTestResultModel>>().apply {
        value = mutableListOf()
    }
    val myopiaTestResultModel: LiveData<List<MyopiaTestResultModel>> = _myopiaTestResultModel

    // Function to add a test result to the list
    fun addHyperopiaTestResult(testResult: HyperopiaTestResultModel) {
        // Create a new mutable list and update the LiveData value
        val updatedList = _hyperopiaTestResultModel.value?.toMutableList() ?: mutableListOf()
        updatedList.add(testResult)
        _hyperopiaTestResultModel.value = updatedList
    }

    fun saveHyperopiaTestResult(testResults: List<HyperopiaTestResultModel>) {
        userRepository.saveHyperopiaTestResult(userId, _eyeTestResult.value?.id!!, testResults)
    }

    fun addMyopiaTestResult(testResult: MyopiaTestResultModel) {
        // Create a new mutable list and update the LiveData value
        val updatedList = _myopiaTestResultModel.value?.toMutableList() ?: mutableListOf()
        updatedList.add(testResult)
        _myopiaTestResultModel.value = updatedList
    }

    fun saveMyopiaTestResult(testResults: List<MyopiaTestResultModel>) {
        userRepository.saveMyopiaTestResult(userId, _eyeTestResult.value?.id!!, testResults)
    }

    // Function to clear all test results from the list
    fun clearTestResults() {
        _hyperopiaTestResultModel.value = mutableListOf()
        _myopiaTestResultModel.value = mutableListOf()
    }
    // <--------------------------------------X-------------------------------------------->
}
