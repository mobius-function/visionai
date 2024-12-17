package com.yuvraj.visionai.ui.home.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.ChatMessages
import com.yuvraj.visionai.databinding.FragmentHomeChatBotBinding
import com.yuvraj.visionai.enums.ChatMessageSender
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_BOT
import com.yuvraj.visionai.ui.home.viewModel.ChatBotViewModel
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastAstigmatismResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastDryEyeResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastHyperopiaResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastMyopiaResults
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
        clickableViews()
        loadPreviousMessages()
        checkForEmptyChat()
        setupScrollListener()
    }


    private fun initViews(view: View) {
        _binding = FragmentHomeChatBotBinding.bind(view)
        chatBotViewModel.resetCurrentPage()

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
                chatBotViewModel.getResponseFromAPI(requireActivity().addUpdateEyeTestResultToQuery(question)) { response ->
                    addResponse(response)
                }

                welcomeText.visibility = View.GONE
            }
        }
    }

    private fun setupScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findFirstVisibleItemPosition() == 0 && dy < 0) {
                    loadMoreMessages()
                }
            }
        })
    }

    private fun loadPreviousMessages() {
        chatBotViewModel.loadMessages {
            // Update the adapter with new messages
            messageAdapter.notifyAddedMessages(chatBotViewModel.messageList.size)
            messageAdapter.notifyDataSetChanged()

            // Debugging
            // Log.d("ChatBotFragment", "Current page: ${chatBotViewModel.currentPage}
            // || Message List Size: ${chatBotViewModel.messageList.size}
            // || Page Size: ${chatBotViewModel.messageLoadedSize.value}")

            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun loadMoreMessages() {
        chatBotViewModel.loadMessages {
            // Update the adapter with new messages
            messageAdapter.notifyAddedMessages(chatBotViewModel.messageList.size)
            messageAdapter.notifyDataSetChanged()

            // Debugging
            // Log.d("ChatBotFragment", "Current page: ${chatBotViewModel.currentPage}
            // || Message List Size: ${chatBotViewModel.messageList.size}
            // || Page Size: ${chatBotViewModel.messageLoadedSize.value}")

            // Scroll to current position
            binding.recyclerView.scrollToPosition(chatBotViewModel.messageLoadedSize.value!!)
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

    private fun Activity.addUpdateEyeTestResultToQuery(query: String) : String {
        val myopiaResults = getPastMyopiaResults()
        val hyperopiaResults = getPastHyperopiaResults()
        val dryLeftEyeResults = if (getPastDryEyeResults().first) "Yes" else "No"
        val dryRightEyeResults = if (getPastDryEyeResults().second) "Yes" else "No"
        val astigmatismResult = if(getPastAstigmatismResults()) "Yes" else "No"

        return "\n\nThe user's Latest Eye results are as follows:\n\n" +
                "Astigmatism: $astigmatismResult\n" +
                "Dry Left Eye: $dryLeftEyeResults\n" +
                "Dry Right Eye: $dryRightEyeResults\n" +
                "Plus Power Left Eye: ${hyperopiaResults.leftEyePower}\n" +
                "Plus Power Right Eye: ${hyperopiaResults.rightEyePower}\n" +
                "Minus Power Left Eye: ${myopiaResults.leftEyePower}\n" +
                "Minus Power Right Eye: ${myopiaResults.leftEyePower}" +
                "\n\n(tailor the response using this information of user's latest eye test results)" +
                "\\n\\nThe query is: $query.\n\n\n"
    }

    private fun checkForEmptyChat() {
        chatBotViewModel.isListEmpty.observe(viewLifecycleOwner) { isEmpty ->
            binding.welcomeText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
}

