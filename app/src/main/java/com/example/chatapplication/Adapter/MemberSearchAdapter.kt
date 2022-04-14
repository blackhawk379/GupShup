package com.example.chatapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.GroupSpecific.GroupInfoActivity
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.UserSpecific.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class MemberSearchAdapter (val context: Context, val userList: ArrayList<Users>, val name: String):
    RecyclerView.Adapter<MemberSearchAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_user_layout, parent, false)
        return UserViewHolder(view)
    }


    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.username.text = currentUser.getUsername()
        FirebaseStorage.getInstance().reference.child("images/profile${currentUser.uid}").downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_profile)
                    .into(holder.img)
            }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, GroupInfoActivity::class.java)
            intent.putExtra("GroupName", name)
            intent.putExtra("memberId", currentUser.uid)
            context.startActivity(intent)
        }
    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById<TextView>(R.id.txtUserSearchProfile)
        val img: CircleImageView = itemView.findViewById<CircleImageView>(R.id.imgUserSearchProfile)
    }
}