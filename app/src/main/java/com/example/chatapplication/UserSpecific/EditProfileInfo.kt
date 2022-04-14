package com.example.chatapplication.UserSpecific

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.MajorActivity.MainActivity2
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class EditProfileInfo : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var fireBaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_info)

        fireBaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)

        val key = intent.getStringExtra("key")

        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())  {
                    var user: Users? = snapshot.getValue(Users::class.java)
                    val etUsername = findViewById<EditText>(R.id.etEditUsername)
                    var etInstagram = findViewById<EditText>(R.id.etEditInstagram)
                    var etFacebook = findViewById<EditText>(R.id.etEditFacebook)
                    var etWebsite = findViewById<EditText>(R.id.etEditWebsite)
                    val btnSave = findViewById<Button>(R.id.btnSaveEdit)
                    if (key != "5"){
                        etUsername.setText(user!!.getUsername())
                        etInstagram.setText(user!!.getInstagram())
                        etFacebook.setText(user!!.getFaceBook())
                        etWebsite.setText(user!!.getWebsite())

                        if (key == "1") etUsername.isEnabled = true
                        if (key == "2") etInstagram.isEnabled = true
                        if (key == "3") etFacebook.isEnabled = true
                        if (key == "4") etWebsite.isEnabled = true

                        btnSave.setOnClickListener {
                            var update: Map<String, String> = mapOf()
                            when (key) {
                                "1" -> if (etUsername.text.toString()!="") update = mapOf("Username" to etUsername.text.toString())
                                "2" -> {
                                    if (etInstagram.text!=null) update = mapOf("Instagram" to etInstagram.text.toString())
                                    else msg("Instagram")
                                }
                                "3" -> {
                                    if (etFacebook.text!=null) update = mapOf("Facebook" to etFacebook.text.toString())
                                    else msg("Facebook")
                                }
                                "4" -> {
                                    if (etWebsite.text!=null) update = mapOf("Website" to etWebsite.text.toString())
                                    else msg("Website")
                                }
                            }
                            refUsers!!.updateChildren(update)
                            val intent = Intent(this@EditProfileInfo, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    else {
                        etUsername.isEnabled = true
                        etInstagram.isEnabled = true
                        etFacebook.isEnabled = true
                        etWebsite.isEnabled = true

                        etUsername.setText(user!!.getUsername())
                        etInstagram.setText("Attach instagram profile")
                        etFacebook.setText("Attach Facebook profile")
                        etWebsite.setText("Attach Website profile")

                    }

                }
            } override fun onCancelled(error: DatabaseError) { Toast.makeText(this@EditProfileInfo, "Error Occurred: $error", Toast.LENGTH_SHORT).show() }
        })

    }

    private fun msg(s: String) { Toast.makeText(this@EditProfileInfo, "$s Profile Detached", Toast.LENGTH_SHORT).show()}
}
