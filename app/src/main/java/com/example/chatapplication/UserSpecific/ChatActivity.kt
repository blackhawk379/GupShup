package com.example.chatapplication.UserSpecific

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.*
import com.example.chatapplication.Adapter.MessageAdapter
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.Model.Message
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var messageBtn: CircleImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbref: DatabaseReference
    private lateinit var mAuth: FirebaseUser
    private lateinit var imageUri: Uri
    private lateinit var name: String
    private lateinit var receiveruid: String

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mDbref = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance().currentUser!!

        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        name = intent.getStringExtra("name").toString()
        receiveruid = intent.getStringExtra("uid").toString()

        senderRoom = receiveruid + mAuth.uid
        receiverRoom = mAuth.uid + receiveruid

        mDbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (message in snapshot.children) messageList.add(message.getValue(Message::class.java)!!)
                    messageAdapter.notifyDataSetChanged()
                } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
            })

        messageBox = findViewById(R.id.etMessageBox)
        messageBtn = findViewById(R.id.btnSendButton)
        messageBtn.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, mAuth.uid!!, false)
            updateRoom(messageObject)
            updateRecent(messageObject)
            messageBox.setText("")
        }

        var imagebtn = findViewById<CircleImageView>(R.id.btnSendImage)
        imagebtn.setOnClickListener { selectImage() }

        toolbar = findViewById(R.id.toolbar_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@ChatActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        toolbar.setOnClickListener {
            val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
            val ref = FirebaseDatabase.getInstance().reference.child("Users").child(receiveruid!!)
            ref.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUser: Users? = snapshot.getValue(Users::class.java)
                    intent.putExtra("name", currentUser?.getUsername())
                    intent.putExtra("uid", currentUser?.uid)
                    intent.putExtra("facebook", currentUser?.getFaceBook())
                    intent.putExtra("instagram", currentUser?.getInstagram())
                    intent.putExtra("website", currentUser?.getWebsite())
                    startActivity(intent)
                } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error")}
            })
        }
    }

    private fun updateRoom(messageObject: Message) {
        mDbref.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject)
            .addOnSuccessListener {
                mDbref.child("chats").child(receiverRoom!!).child("messages").push().setValue(messageObject)
            }
    }

    private fun updateRecent(messageObject: Message) {
        mDbref.child("chats").child(senderRoom!!).child("recent").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbref.child("chats").child(receiverRoom!!).child("recent").push()
                    .setValue(messageObject)
            }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uid = mAuth.uid
        if(requestCode == 100 && resultCode == RESULT_OK) {
            var bar = findViewById<ProgressBar>(R.id.chatProgressBar)
            bar.visibility = View.VISIBLE
            imageUri = data?.data!!
            val sdf = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")
            val currentDate = sdf.format(Date())
            val sdg = SimpleDateFormat("ddMMyyyyhhmmss")
            FirebaseStorage.getInstance().getReference("images/$currentDate$uid").putFile(imageUri).addOnSuccessListener {
                msg("Upload Successful")
                FirebaseStorage.getInstance().reference.child("images/$currentDate$uid").downloadUrl
                    .addOnSuccessListener {
                        val messageObject = Message(it!!.toString(), uid!!, true)
                        mDbref.child("chats").child(senderRoom!!).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                mDbref.child("chats").child(receiverRoom!!).child("messages").push()
                                    .setValue(messageObject)
                            }
                        msg("Download Successful")
                        bar.visibility = View.INVISIBLE
                    } .addOnFailureListener{ msg("Download Error: $it") }
            } .addOnFailureListener{ msg("Upload Error: $it") }
        }
    }

    private fun msg(msg: String) = Toast.makeText(this@ChatActivity, msg, Toast.LENGTH_SHORT).show()
}