package com.yuvraj.visionai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuvraj.visionai.databinding.ItemChatBinding
import com.yuvraj.visionai.model.ChatMessage
import com.yuvraj.visionai.enums.ChatMessageSender.SENT_BY_ME

class ChatMessages(
    private var messageList :List<ChatMessage>
): RecyclerView.Adapter<ChatMessages.ViewHolder>()
{
    inner class ViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            if (message.sender == SENT_BY_ME){
                binding.leftChatView.visibility = View.GONE
                binding.rightChatView.visibility = View.VISIBLE
                binding.rightChatTextView.text = message.message
            }else{
                binding.leftChatView.visibility = View.VISIBLE
                binding.rightChatView.visibility = View.GONE
                binding.leftChatTextView.text = message.message
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessages.ViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messageList[position])

        holder.itemView.setOnClickListener {
            // TODO: OnClick implementation for each message in the list
        }
    }
}