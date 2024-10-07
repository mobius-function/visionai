package com.yuvraj.visionai.ui.home.viewModel

import android.util.Log
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

    private var currentPage = 0

    val messageList: MutableList<ChatMessage> = mutableListOf()
    val isListEmpty = MutableLiveData(true)
    val messageLoadedSize = MutableLiveData(0)

    // Load initial messages or next page
    fun loadMessages(onLoaded: () -> Unit) {
        viewModelScope.launch {
            val offset = currentPage * PAGE_SIZE
            val messages = chatMessageDao.getMessagesWithPagination(PAGE_SIZE, offset)

            // Convert the list of ChatMessageEntity to ChatMessage
            val newMessages : MutableList<ChatMessage> = mutableListOf()
            messages.forEach { entity ->
                newMessages.add(
                    ChatMessage(
                        entity.message,
                        if (entity.sentBy == "SENT_BY_ME") ChatMessageSender.SENT_BY_ME else ChatMessageSender.SENT_BY_BOT
                    )
                )
            }
            messageLoadedSize.value = newMessages.size
            messageList.addAll(0, newMessages)
            currentPage++
            onLoaded()
        }
    }

    fun addMessageToChat(message: String, sentBy: ChatMessageSender, onAdded: () -> Unit) {
        viewModelScope.launch {
            messageList.add(ChatMessage(message, sentBy))

            if(sentBy == ChatMessageSender.SENT_BY_BOT && message == "Typing...") {
                // Dont log in the local database
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

    fun resetCurrentPage() {
        currentPage = 0
        messageList.clear()  // Optionally clear the previous messages if you want to start fresh
    }

    companion object {
        private const val PAGE_SIZE = 40
    }
}