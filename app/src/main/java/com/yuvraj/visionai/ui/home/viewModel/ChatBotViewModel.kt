package com.yuvraj.visionai.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yuvraj.visionai.db.dao.ChatMessageDao
import com.yuvraj.visionai.db.entities.ChatMessageEntity
import androidx.lifecycle.viewModelScope
import com.yuvraj.visionai.enums.ChatMessageSender
import com.yuvraj.visionai.model.ChatMessage
import com.yuvraj.visionai.repositories.ChatResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val chatMessageDao: ChatMessageDao
) : ViewModel() {

    val messageList: MutableList<ChatMessage> = mutableListOf()
    val isListEmpty = MutableLiveData(true)

    fun loadPreviousMessages(onLoaded: () -> Unit) {
        viewModelScope.launch {
            val messages = chatMessageDao.getAllMessages()
            if (messages.isEmpty()) {
                isListEmpty.value = true
                onLoaded()
                return@launch
            } else {
                isListEmpty.value = false
            }
            messages.forEach { entity ->
                messageList.add(
                    ChatMessage(
                        entity.message,
                        if (entity.sentBy == "SENT_BY_ME") ChatMessageSender.SENT_BY_ME else ChatMessageSender.SENT_BY_BOT
                    )
                )
            }
            onLoaded()
        }
    }

    fun addMessageToChat(message: String, sentBy: ChatMessageSender, onAdded: () -> Unit) {
        viewModelScope.launch {
            messageList.add(ChatMessage(message, sentBy))

            if(sentBy == ChatMessageSender.SENT_BY_BOT && message == "Typing...") {
                // Dont log
            } else {
                chatMessageDao.insertMessage(
                    ChatMessageEntity(
                        message = message,
                        sentBy = sentBy.name
                    )
                )
            }

            onAdded()
        }
    }

    fun getResponseFromAPI(question: String, onResponse: (String) -> Unit) {
        ChatResponse.getResFun(question) { response ->
            onResponse(response)
        }
    }
}