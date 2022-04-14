package com.example.chatapplication.GroupSpecific

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.GroupMessageAdapter
import com.example.chatapplication.Adapter.MessageAdapter
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.Model.GroupMessages
import com.example.chatapplication.Model.Message
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class GroupChatActivity : AppCompatActivity() {

    private lateinit var refUsers: DatabaseReference
    private lateinit var refChats: DatabaseReference
    private lateinit var fireBaseUser: FirebaseUser
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var messageBtn: CircleImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var groupMessageAdapter: GroupMessageAdapter
    private lateinit var messageList: ArrayList<GroupMessages>
    private lateinit var mDbref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        messageRecyclerView = findViewById(R.id.chatRecyclerViewb)
        messageBox = findViewById(R.id.etMessageBoxb)
        messageBtn = findViewById(R.id.btnSendButtonb)
        toolbar = findViewById(R.id.toolbar_chatb)
        messageList = ArrayList()
        groupMessageAdapter = GroupMessageAdapter(this, messageList)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = groupMessageAdapter

        mDbref = FirebaseDatabase.getInstance().reference
        fireBaseUser = FirebaseAuth.getInstance().currentUser!!
        refUsers = mDbref.child("Users").child(fireBaseUser!!.uid)

        val name = intent.getStringExtra("GroupName")
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid
        refChats = mDbref.child("chats").child(name!!).child("messages")
        refChats.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@GroupChatActivity,
                        "Error Occurred!! $error",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(GroupMessages::class.java)
                        messageList.add(message!!)
                    }
                    groupMessageAdapter.notifyDataSetChanged()
                }

            })

        messageBtn.setOnClickListener {
            val message = messageBox.text.toString()
            println("HERE")
            refUsers!!.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@GroupChatActivity,
                        "Error Occurred!! $error",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user: Users? = snapshot.getValue(Users::class.java)
                        val senderName = user?.getUsername().toString()
                        val messageObject = GroupMessages(message, senderuid!!, senderName)
                        refChats.push().setValue(messageObject)
                        messageBox.setText("")
                    }
                }

            })


        }

        setSupportActionBar(toolbar)
        supportActionBar!!.title = name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setOnClickListener {
            val intent = Intent(this@GroupChatActivity, GroupInfoActivity::class.java)
            intent.putExtra("GroupName", name)
            startActivity(intent)
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this@GroupChatActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
