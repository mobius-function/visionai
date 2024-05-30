package com.yuvraj.visionai.model

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InferenceChatModel private constructor(context: Context) {

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()



    companion object {
        private const val MODEL_PATH = "/data/local/tmp/llm/model.bin"
        private var instance: InferenceChatModel? = null

        fun getInstance(context: Context): InferenceChatModel {
            return if (instance != null) {
                instance!!
            } else {
                InferenceChatModel(context).also { instance = it }
            }
        }
    }
}