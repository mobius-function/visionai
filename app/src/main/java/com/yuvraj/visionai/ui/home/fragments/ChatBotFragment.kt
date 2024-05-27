package com.yuvraj.visionai.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuvraj.visionai.R
import com.yuvraj.visionai.adapters.ChatMessages
import com.yuvraj.visionai.databinding.FragmentHomeChatBotBinding
import com.yuvraj.visionai.databinding.FragmentHomeStatisticsBinding
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_BOT
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_ME
import com.yuvraj.visionai.model.ChatMessage
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject


class ChatBotFragment : Fragment(R.layout.fragment_home_chat_bot) {

    private var _binding: FragmentHomeChatBotBinding? = null
    private val binding get() = _binding!!

    lateinit var messageList : MutableList<ChatMessage>
    lateinit var messageAdapter : ChatMessages
    val client = OkHttpClient()


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
                addToChat(question,SENT_BY_ME)
                inputMessage.setText("")
                callAPI(question)
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

    fun addResponse(response:String?){
        messageList.removeAt(messageList.size -1)
        addToChat(response!!, SENT_BY_BOT)

    }

    private fun callAPI(question: String) {
        // TODO: Implement API call

    }
    companion object{
        val JSON : MediaType = "application/json; charset=utf-8".toMediaType()
    }
}

