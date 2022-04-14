package com.example.chatapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Model.Message
import com.example.chatapplication.UserSpecific.ChatActivity
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.UserSpecific.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class UserAdapter(val context: Context, val userList: ArrayList<Users>):
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.username.text = currentUser.getUsername()
        var mAuth = FirebaseAuth.getInstance()
        var user = mAuth.currentUser
        var mDbref = FirebaseDatabase.getInstance().reference
        var room = user?.uid + currentUser.uid
        mDbref.child("chats").child(room!!).child("recent")
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message?.senderId==user?.uid) {
                            if (message?.isImage == false) holder.recent.text = "Me: ${message.message}"
                            else holder.recent.text = "Me: Image"
                        }
                        else{
                            if (message?.isImage == false) holder.recent.text = "${currentUser.getUsername()}: ${message.message}"
                            else holder.recent.text = "${currentUser.getUsername()}: Image"
                        }
                    }
                } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }

            })
        FirebaseStorage.getInstance().reference.child("images/profile${currentUser.uid}").downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_profile)
                    .into(holder.img)
            }
        holder.itemView.setOnLongClickListener {
            val mDialogView = View.inflate(context, R.layout.dialog_profile, null)
            val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.attributes?.windowAnimations = R.style.PauseDialog
            var profile = mDialogView.findViewById<ImageView>(R.id.imgProfileDialog)
            FirebaseStorage.getInstance().reference.child("images/profile${currentUser.uid}").downloadUrl
                .addOnSuccessListener {
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_profile)
                        .into(profile)
                }
            var cover = mDialogView.findViewById<ImageView>(R.id.imgCoverDialog)
            FirebaseStorage.getInstance().reference.child("images/cover${currentUser.uid}").downloadUrl
                .addOnSuccessListener {
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_cover)
                        .into(cover)
                }

            //For new users
            var attachLinks = currentUser.getAttachLinks()
            var attachIcons = currentUser.getAttachIcons()
            if ("ABC" in attachLinks) attachLinks.remove("ABC")
            if (123 in attachIcons) attachIcons.remove(123)

            //For existing users
            val fb = currentUser.getFaceBook()
            val insta = currentUser.getInstagram()
            val web = currentUser.getWebsite()

            if (fb!="https://www.facebook.com/" && fb !in attachLinks) {
                attachLinks.add(fb!!)
                attachIcons.add(R.drawable.fb)
            }
            if (insta!="https://www.instagram.com/" && insta !in attachLinks) {
                attachLinks.add(insta!!)
                attachIcons.add(R.drawable.insta)
            }
            if (web!="https://www.google.com/" && web !in attachLinks) {
                attachLinks.add(web!!)
                attachIcons.add(R.drawable.website)
            }

            val recyclerView = mDialogView.findViewById<RecyclerView>(R.id.recyclerViewDialog)
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = DialogAdapter(context, attachLinks, attachIcons)

            mDialogView.findViewById<TextView>(R.id.txtProfileDialog).text = currentUser.getUsername()
            mDialogView.findViewById<ProgressBar>(R.id.dialogProgressbar).visibility = INVISIBLE
            mDialogView.findViewById<Button>(R.id.btnToProfile).setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("name", currentUser.getUsername())
                intent.putExtra("uid", currentUser.uid)
                intent.putExtra("facebook", currentUser.getFaceBook())
                intent.putExtra("instagram", currentUser.getInstagram())
                intent.putExtra("website", currentUser.getWebsite())
                context.startActivity(intent)
            }
            mDialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnToBack)
                    .setOnClickListener{
                        mAlertDialog.dismiss()
                    }
            true

        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
        }
    }

    private fun msg(s: String) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show()}

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txtUserProfile)
        val img: CircleImageView = itemView.findViewById(R.id.imgUserProfile)
        val recent: TextView = itemView.findViewById(R.id.txtRecentChat)
    }
}