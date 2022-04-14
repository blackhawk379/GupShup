package com.example.chatapplication.UserSpecific

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapplication.MajorActivity.MainActivity2
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CreateProfileActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var fireBaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        fireBaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)

        val key = intent.getStringExtra("key")

        refUsers!!.addValueEventListener(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var user: Users? = snapshot.getValue(Users::class.java)
                    val etUsername = findViewById<EditText>(R.id.etEditUsernameCreate)
                    var etInstagram = findViewById<EditText>(R.id.etEditInstagramCreate)
                    var etFacebook = findViewById<EditText>(R.id.etEditFacebookCreate)
                    var etWebsite = findViewById<EditText>(R.id.etEditWebsiteCreate)
                    val btnSave = findViewById<Button>(R.id.btnSaveEditCreate)
                    btnSave.setOnClickListener {
                        var update: Map<String, String>
                        var attachLinks = user?.getAttachLinks()
                        var attachIcons = user?.getAttachIcons()

                        if (etUsername.text.toString()=="") msg("Username is mandatory field")
                        else {
                            if (etUsername.text.toString()!="") {
                                update = mapOf("Username" to etUsername.text.toString())
                                refUsers!!.updateChildren(update)
                            }
                            if (etInstagram.text!=null) {
                                attachLinks?.add(etInstagram.text.toString())
                                attachIcons?.add(R.drawable.insta)
                                update = mapOf("Instagram" to etInstagram.text.toString())
                                refUsers!!.updateChildren(update)
                            }
                            if (etFacebook.text!=null) {
                                attachLinks?.add(etFacebook.text.toString())
                                attachIcons?.add(R.drawable.fb)
                                update = mapOf("Facebook" to etFacebook.text.toString())
                                refUsers!!.updateChildren(update)
                            }
                            if (etWebsite.text!=null) {
                                attachLinks?.add(etWebsite.text.toString())
                                attachIcons?.add(R.drawable.website)
                                update = mapOf("Website" to etWebsite.text.toString())
                                refUsers!!.updateChildren(update)
                            }
                            var update1 = mapOf("AttachLinks" to attachLinks)
                            var update2 = mapOf("AttachIcons" to attachIcons)
                            refUsers!!.updateChildren(update1)
                            refUsers!!.updateChildren(update2)
                            val intent = Intent(this@CreateProfileActivity, MainActivity2::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })
    }
    private fun msg(s: String) { Toast.makeText(this@CreateProfileActivity, s, Toast.LENGTH_SHORT).show()}
}