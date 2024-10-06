package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.ChatMessages
import com.yuvraj.visionai.databinding.FragmentHomeChatBotBinding
import com.yuvraj.visionai.enums.ChatMessageSender
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_BOT
import com.yuvraj.visionai.ui.home.viewModel.ChatBotViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatBotFragment : Fragment(R.layout.fragment_home_chat_bot) {

    private var _binding: FragmentHomeChatBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: ChatMessages
    private val chatBotViewModel: ChatBotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        loadPreviousMessages()
        clickableViews()
        checkForEmptyChat()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeChatBotBinding.bind(view)

        messageAdapter = ChatMessages(chatBotViewModel.messageList)

        binding.apply {
            recyclerView.adapter = messageAdapter
            val layoutManager = LinearLayoutManager(requireActivity())
            layoutManager.stackFromEnd = true
            recyclerView.layoutManager = layoutManager
        }

    }

    private fun clickableViews() {
        binding.apply {
            btnSend.setOnClickListener {
                val question = inputMessage.text.toString().trim { it <= ' ' }
                if (question.isEmpty()) return@setOnClickListener

                // Add user's message to the chat and save it to the database
                chatBotViewModel.addMessageToChat(question, ChatMessageSender.SENT_BY_ME) {
                    messageAdapter.notifyDataSetChanged()
                    binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
                }

                // Clear the input field and show "Typing..." message
                inputMessage.setText("")

                chatBotViewModel.addMessageToChat("Typing...", SENT_BY_BOT) {
                    messageAdapter.notifyDataSetChanged()
                    binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
                }

                // Get response from the API
                chatBotViewModel.getResponseFromAPI(question) { response ->
                    addResponse(response)
                }

                welcomeText.visibility = View.GONE
            }

        }
    }

    private fun loadPreviousMessages() {
        chatBotViewModel.loadPreviousMessages {
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        // Remove the "Typing..." message and add the bot's response
        chatBotViewModel.messageList.removeAt(chatBotViewModel.messageList.size - 1)

        // adds the Bot's response to the chat
        chatBotViewModel.addMessageToChat(response, SENT_BY_BOT) {
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun checkForEmptyChat() {
        if (messageAdapter.itemCount == 0) {
            binding.welcomeText.visibility = View.VISIBLE
        } else {
            binding.welcomeText.visibility = View.GONE
        }
    }
}

