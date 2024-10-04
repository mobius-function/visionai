package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.ChatMessages
import com.yuvraj.visionai.databinding.FragmentHomeChatBotBinding
import com.yuvraj.visionai.db.dao.ChatMessageDao
import com.yuvraj.visionai.db.entities.ChatMessageEntity
import com.yuvraj.visionai.enums.ChatMessageSender
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_BOT
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_ME
import com.yuvraj.visionai.model.ChatMessage
import com.yuvraj.visionai.repositories.ChatResponse
import com.yuvraj.visionai.repositories.ChatResponse.getResFun
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatBotFragment : Fragment(R.layout.fragment_home_chat_bot) {

    @Inject
    lateinit var chatMessageDao: ChatMessageDao

    private var _binding: FragmentHomeChatBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageList : MutableList<ChatMessage>
    private lateinit var messageAdapter : ChatMessages


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        loadPreviousMessages()
        clickableViews()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeChatBotBinding.bind(view)

        messageList = ArrayList()
        messageAdapter = ChatMessages(messageList)

        binding.apply{
            recyclerView.adapter = messageAdapter
            val layoutManger = LinearLayoutManager(requireActivity())
            layoutManger.stackFromEnd = true
            recyclerView.layoutManager = layoutManger
        }

    }

    private fun clickableViews() {
        binding.apply {
            btnSend.setOnClickListener {
//                val question = inputMessage.text.toString().trim{ it <= ' '}
//                if(question.isEmpty()){
//                    return@setOnClickListener
//                }
//
//                addToChat(question,SENT_BY_ME)
//                inputMessage.setText("")
//                messageList.add(ChatMessage("Typing...", SENT_BY_BOT))
//
//                getResFun(question, ::addResponse)
//
//                welcomeText.visibility = View.GONE

                val question = inputMessage.text.toString().trim{ it <= ' ' }
                if (question.isEmpty()) return@setOnClickListener

                addToChat(question, ChatMessageSender.SENT_BY_ME)
                inputMessage.setText("")

                messageList.add(ChatMessage("Typing...", ChatMessageSender.SENT_BY_BOT))

                // Store user's message in the database
                lifecycleScope.launch {
                    chatMessageDao.insertMessage(ChatMessageEntity(message = question, sentBy = "SENT_BY_ME"))
                }

                // Get response
                getResFun(question) { response ->
                    addResponse(response)
                }

                welcomeText.visibility = View.GONE
            }

        }
    }

//    private fun addToChat(message: String, sentBy: ChatMessageSender) {
//        requireActivity().runOnUiThread{
//            messageList.add(ChatMessage(message,sentBy))
//            messageAdapter.notifyDataSetChanged()
//            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
//        }
//
//    }
//
//    private fun addResponse(response:String?){
//        // removes the "Typing..." message
//        messageList.removeAt(messageList.size -1)
//
//        // adds the response to the chat
//        addToChat(response!!, SENT_BY_BOT)
//    }

    private fun loadPreviousMessages() {
        lifecycleScope.launch {
            val messages = chatMessageDao.getAllMessages()
            if(messages.isEmpty()) return@launch
            messages.forEach { entity ->
                messageList.add(
                    ChatMessage(entity.message,
                        if (entity.sentBy == "SENT_BY_ME") ChatMessageSender.SENT_BY_ME
                        else ChatMessageSender.SENT_BY_BOT)
                )
            }
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addToChat(message: String, sentBy: ChatMessageSender) {
        requireActivity().runOnUiThread {
            messageList.add(ChatMessage(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)

            // Save to database
            lifecycleScope.launch {
                chatMessageDao.insertMessage(ChatMessageEntity(message = message, sentBy = sentBy.name))
            }
        }
    }

    private fun addResponse(response: String?) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response!!, SENT_BY_BOT)
    }
}

