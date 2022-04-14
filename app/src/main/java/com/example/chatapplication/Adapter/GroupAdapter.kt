package com.example.chatapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.GroupSpecific.GroupChatActivity
import com.example.chatapplication.GroupSpecific.GroupInfoActivity
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class GroupAdapter (val context: Context, val groupList: ArrayList<Groups>):
    RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_layout, parent, false)
        return GroupViewHolder(view)
    }


    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.groupname.text = currentGroup.getGroupname()
        holder.itemView.isLongClickable
        holder.itemView.setOnLongClickListener {
            val intent = Intent(context, GroupInfoActivity::class.java)
            intent.putExtra("GroupName", currentGroup.getGroupname())
            context.startActivity(intent)
            true
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("GroupName", currentGroup.getGroupname())
            context.startActivity(intent)
        }
    }
    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupname: TextView = itemView.findViewById(R.id.txtGroupname)
        val img: CircleImageView = itemView.findViewById(R.id.imgGroupProfile)
    }
}