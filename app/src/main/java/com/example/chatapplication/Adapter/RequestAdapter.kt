package com.example.chatapplication.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.GroupSpecific.GroupInfoActivity
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.UserSpecific.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class RequestAdapter(val context: Context, val userList: ArrayList<Users>):
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_user_layout, parent, false)
        return RequestViewHolder(view)
    }


    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
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
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("facebook", currentUser.getFaceBook())
            intent.putExtra("instagram", currentUser.getInstagram())
            intent.putExtra("website", currentUser.getWebsite())
            context.startActivity(intent)
        }
    }
    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById<TextView>(R.id.txtUserSearchProfile)
        val img: CircleImageView = itemView.findViewById<CircleImageView>(R.id.imgUserSearchProfile)
    }
}