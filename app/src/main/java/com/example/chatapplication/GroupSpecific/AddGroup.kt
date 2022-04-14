package com.example.chatapplication.GroupSpecific

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.MajorActivity.WelcomeActivity
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddGroup : AppCompatActivity() {

    private lateinit var refUsers: DatabaseReference
    private lateinit var refGroups: DatabaseReference
    lateinit var members: ArrayList<String>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        var NewGroupToolbar = findViewById<Toolbar>(R.id.NewGroupToolbar)
        setSupportActionBar(NewGroupToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        NewGroupToolbar.setNavigationOnClickListener {
            val intent = Intent(this@AddGroup, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        var btnCreateGroup = findViewById<Button>(R.id.btnCreateGroup)

        btnCreateGroup.setOnClickListener {
            CreateGroup()
            val intent = Intent(this@AddGroup, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun CreateGroup() {
        var etGroupname = findViewById<EditText>(R.id.etGroupname)
        var etGroupDescription = findViewById<EditText>(R.id.etGroupDescription)

        val Groupname: String = etGroupname.text.toString()
        val GroupDescription: String = etGroupDescription.text.toString()

        if (Groupname == "") Toast.makeText(
            this@AddGroup,
            "Please Enter a Username",
            Toast.LENGTH_LONG
        ).show()
        else {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            refGroups = FirebaseDatabase.getInstance().reference.child("Groups").child(Groupname)
            members = ArrayList()
            members.add(uid)
            val groupHashMap = HashMap<String, Any>()
            groupHashMap["Admin"] = uid
            groupHashMap["Groupname"] = Groupname
            groupHashMap["GroupDescription"] = GroupDescription
            groupHashMap["Search"] = Groupname.toLowerCase()
            groupHashMap["Members"] = members
            refGroups.updateChildren(groupHashMap)
            refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
            refUsers!!.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val group = user?.getGroups()
                    if (Groupname in group!!) {
                        val intent = Intent(this@AddGroup, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        group.add(Groupname)
                        val update: Map<String, ArrayList<String>?> = mapOf("Groups" to group)
                        refUsers!!.updateChildren(update)
                    }
                }

            })

        }
    }
}