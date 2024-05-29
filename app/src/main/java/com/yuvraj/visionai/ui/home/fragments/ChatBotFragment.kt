package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.ChatMessages
import com.yuvraj.visionai.databinding.FragmentHomeChatBotBinding
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_BOT
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_ME
import com.yuvraj.visionai.model.ChatMessage
import com.yuvraj.visionai.repositories.ChatResponse.getChatResponse
import com.yuvraj.visionai.repositories.ChatResponse.getResFun
import com.yuvraj.visionai.utils.Constants.CHAT_BOT_API_KEY
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ChatBotFragment : Fragment(R.layout.fragment_home_chat_bot) {

    private var _binding: FragmentHomeChatBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageList : MutableList<ChatMessage>
    private lateinit var messageAdapter : ChatMessages


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
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
                val question = inputMessage.text.toString().trim{ it <= ' '}
                if(question.isEmpty()){
                    return@setOnClickListener
                }

                addToChat(question,SENT_BY_ME)
                inputMessage.setText("")
                messageList.add(ChatMessage("Typing...", SENT_BY_BOT))

//                getChatResponse(question, ::addResponse)
//                callAPI(question)
                getResFun(question, ::addResponse)

                welcomeText.visibility = View.GONE
            }

        }
    }

    private fun addToChat(message: String, sentBy: String) {
        requireActivity().runOnUiThread{
            messageList.add(ChatMessage(message,sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }

    }

    private fun addResponse(response:String?){
        // removes the "Typing..." message
        messageList.removeAt(messageList.size -1)

        // adds the response to the chat
        addToChat(response!!, SENT_BY_BOT)
    }
}

