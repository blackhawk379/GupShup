package com.example.chatapplication.UserSpecific

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var name: String
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        name = intent.getStringExtra("name").toString()

        toolbar = findViewById(R.id.toolbar_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            toolbar.setNavigationOnClickListener { intentToMain() }

        uid = intent.getStringExtra("uid").toString()
        val facebook = intent.getStringExtra("facebook")
        val instagram = intent.getStringExtra("instagram")
        val website = intent.getStringExtra("website")

        val username = findViewById<TextView>(R.id.txtProfileB)
        val cover = findViewById<ImageView>(R.id.imgCoverB)
        val profile = findViewById<ImageView>(R.id.imgProfileB)

        val progressBar = findViewById<ProgressBar>(R.id.profileProgressbarB)
        progressBar?.visibility = View.VISIBLE

        username?.text = name
        FirebaseStorage.getInstance().reference.child("images/cover$uid").downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_cover)
                    .into(cover)
            }
        FirebaseStorage.getInstance().reference.child("images/profile$uid").downloadUrl
            .addOnSuccessListener {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_profile)
                    .into(profile)
            }

        val imgProfileInsta = findViewById<CircleImageView>(R.id.imgProfileInstaB)
        val imgProfileFaceBook = findViewById<CircleImageView>(R.id.imgProfileFaceBookB)
        val imgProfileWebsite = findViewById<CircleImageView>(R.id.imgProfileWebsiteB)

        imgProfileInsta?.setOnClickListener { browserIntent(instagram) }
        imgProfileFaceBook?.setOnClickListener { browserIntent(facebook) }
        imgProfileWebsite?.setOnClickListener { browserIntent(website) }

        progressBar?.visibility = View.INVISIBLE
        val btnMessage = findViewById<Button>(R.id.btnMessage)
        btnMessage.setOnClickListener { intentToChat() }

        var mAuth = FirebaseAuth.getInstance().currentUser?.uid
        val btnRequest = findViewById<Button>(R.id.btnRequest)
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(uid!!)
        val refUsers1 = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!)
        var clock1 = 0
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                var requests = user?.getRequests()
                var friends = user?.getFriends()
                if (clock1==0){
                    if (mAuth.toString() in friends!!) {
                        btnRequest.text = "Break Up"
                        clock1+=1
                    }
                    if (mAuth.toString() in requests!!) {
                        btnRequest.text = "Requested"
                        clock1+=1
                    }
                }
                if (user?.uid == mAuth){
                    btnMessage.text = "Welcome"
                    btnRequest.text = user.getUsername().toString()
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })
        refUsers1.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user: Users? = snapshot.getValue(Users::class.java)
                var requests = user?.getRequests()
                if (uid in requests!! && clock1==0) {
                    btnRequest.text = "Accept"
                    clock1+=1
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }

        })
        btnRequest.setOnClickListener {
            var clock = 0
            when (btnRequest.text) {
                "Break Up" -> {
                    refUsers.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: Users? = snapshot.getValue(Users::class.java)
                            var friends = user?.getFriends()
                            var name = user?.getUsername()
                            if (mAuth in friends!! && clock==0) {
                                println("-----$mAuth removed from $name friend list----")
                                friends.remove(mAuth)
                                var update = mapOf("Friends" to friends)
                                refUsers.updateChildren(update)
                            }
                            refUsers1!!.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user: Users? = snapshot.getValue(Users::class.java)
                                    var friends = user?.getFriends()
                                    var name1 = user?.getUsername()
                                    if (uid in friends!! && clock == 0) {
                                        println("-----$name removed from $name1 friend list----")
                                        friends.remove(uid)
                                        var update = mapOf("Friends" to friends)
                                        refUsers1.updateChildren(update)
                                        btnRequest.text = "Request"
                                        clock+=1
                                        msg("Broken Up!!!")
                                    }
                                } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                            })
                        } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                    })
                }
                "Requested" -> msg("Already Requested!!!")
                "Accept" -> {
                    refUsers.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: Users? = snapshot.getValue(Users::class.java)
                            var friends = user?.getFriends()
                            var name = user?.getUsername()
                            if (mAuth !in friends!! && clock==0) {
                                println("-----$mAuth added to $name friend list----")
                                friends.add(mAuth)
                                var update = mapOf("Friends" to friends)
                                refUsers.updateChildren(update)
                            }
                            refUsers1!!.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user: Users? = snapshot.getValue(Users::class.java)
                                    var friends = user?.getFriends()
                                    var requests = user?.getRequests()
                                    var name1 = user?.getUsername()
                                    if (uid !in friends!! && clock == 0) {
                                        println("-----$name added to $name1 friend list----")
                                        friends.add(uid)
                                        var update = mapOf("Friends" to friends)
                                        refUsers1.updateChildren(update)
                                    }
                                    if (uid in requests!! && clock == 0) {
                                        println("-----$name removed from $name1 Request list----")
                                        requests.remove(uid)
                                        var update = mapOf("Requests" to requests)
                                        refUsers1.updateChildren(update)
                                        btnRequest.text = "Break Up"
                                        msg("You're now Friends!")
                                        clock+=1
                                    }
                                } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                            })
                        } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                    })
                }
                "Request" -> {
                    refUsers.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user: Users? = snapshot.getValue(Users::class.java)
                            var requests = user?.getRequests()
                            var name = user?.getUsername()
                            if (mAuth !in requests!! && clock==0) {
                                println("----$mAuth added to $name request list---")
                                requests.add(mAuth)
                                var update = mapOf("Requests" to requests)
                                refUsers.updateChildren(update)
                                msg("Requested!!!")
                                btnRequest.text = "Requested"
                                clock+=1
                            }
                        } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                    })
                }
            }
        }
    }

    private fun intentToChat() {
        val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("uid", uid)
        startActivity(intent)
        finish()
    }

    private fun browserIntent(website: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
        startActivity(browserIntent)
    }

    private fun intentToMain() {
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun msg(msg: String) = Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_SHORT).show()
}