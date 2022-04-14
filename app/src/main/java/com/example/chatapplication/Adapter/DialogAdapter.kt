package com.example.chatapplication.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class DialogAdapter(val context: Context, val linksList: ArrayList<String>, val iconsList: ArrayList<Int>):
    RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.test_layout, parent, false)
        return DialogAdapter.DialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.img.setImageResource(iconsList[position])
        holder.img.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linksList[position]))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
        return linksList.size
    }

    class DialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: CircleImageView = itemView.findViewById(R.id.imgTest)
    }

}