package com.example.chatapplication.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.Model.Message
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val IMAGE_RECEIVE = 3
    val IMAGE_SENT = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.received, parent, false)
                return ReceiveViewHolder(view)
            }
            2 -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sent, parent, false)
                return SentViewHolder(view)
            }
            3 -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.received_image, parent, false)
                return ReceiveImageViewHolder(view)
            }
            else -> {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sent_image, parent, false)
                return SentImageViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (currentMessage.isImage == true){
            return if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid) IMAGE_SENT
            else IMAGE_RECEIVE
        }
        return if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid) ITEM_SENT
        else ITEM_RECEIVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when (holder.javaClass) {
            SentViewHolder::class.java -> {
                holder as SentViewHolder
                holder.sentMessage.text = currentMessage.message
            }
            ReceiveViewHolder::class.java -> {
                holder as ReceiveViewHolder
                holder.receivedMessage.text = currentMessage.message
            }
            SentImageViewHolder::class.java -> {
                holder as SentImageViewHolder
                Picasso.get()
                    .load(Uri.parse(currentMessage.message))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.sentImage)
                holder.sentImage.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentMessage.message))
                    context.startActivity(browserIntent)
                }
            }
            else -> {
                holder as ReceiveImageViewHolder
                Picasso.get()
                    .load(Uri.parse(currentMessage.message))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.receivedImage)
                holder.receivedImage.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentMessage.message))
                    context.startActivity(browserIntent)
                }
            }
        }
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txtSentMessage)
    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receivedMessage: TextView = itemView.findViewById(R.id.txtReceivedMessage)
    }
    class SentImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentImage: ImageView = itemView.findViewById(R.id.imgSent)
    }
    class ReceiveImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receivedImage: ImageView = itemView.findViewById(R.id.imgReceived)
    }
}

