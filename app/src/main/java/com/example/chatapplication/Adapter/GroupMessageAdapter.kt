package com.example.chatapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Model.GroupMessages
import com.example.chatapplication.Model.Message
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth

class GroupMessageAdapter (val context: Context, val messageList: ArrayList<GroupMessages>):
RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_received, parent, false)
            ReceiveViewHolder(view)
        }
        else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sent, parent, false)
            SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid){
            ITEM_SENT
        }
        else{
            ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        }
        else{
            val viewHolder =  holder as ReceiveViewHolder
            holder.receivedMessage.text = currentMessage.message
            println("------------${currentMessage.senderName}---------")
            holder.senderName.text = currentMessage.senderName
        }
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txtSentMessage)
    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receivedMessage: TextView = itemView.findViewById(R.id.txtReceivedMessage)
        val senderName: TextView = itemView.findViewById(R.id.txtSender)
    }
}