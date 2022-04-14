package com.example.chatapplication.MajorActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        val registerBtn = findViewById<Button>(R.id.register_btn)
        registerBtn.setOnClickListener {
            registerUser()
        }



    }
    private fun registerUser() {

        val usernameRegister = findViewById<EditText>(R.id.username_register)
        val emailRegister = findViewById<EditText>(R.id.email_register)
        val passwordRegister = findViewById<EditText>(R.id.password_register)
        val progressBar = findViewById<ProgressBar>(R.id.progress_Bar)

        val username: String = usernameRegister.text.toString()
        val email: String = emailRegister.text.toString()
        val password: String = passwordRegister.text.toString()

        when{
            username == "" -> Toast.makeText(this@RegisterActivity, "Please Enter a Username", Toast.LENGTH_LONG).show()
            email == "" -> Toast.makeText(this@RegisterActivity, "Please Enter an Email ID", Toast.LENGTH_LONG).show()
            password == "" -> Toast.makeText(this@RegisterActivity, "Please Enter a Password", Toast.LENGTH_LONG).show()
            else -> {
                progressBar.visibility = View.VISIBLE
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            firebaseUserId = mAuth.currentUser!!.uid
                            var use = mAuth.currentUser
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)
                            var groups: ArrayList<String> = ArrayList()
                            groups.add("ABC")
                            var friends: ArrayList<String> = ArrayList()
                            friends.add("ABC")
                            var requests: ArrayList<String> = ArrayList()
                            requests.add("ABC")
                            var requested: ArrayList<String> = ArrayList()
                            requested.add("ABC")
                            var attachLinks: ArrayList<String> = ArrayList()
                            // attachLinks.add("ABC")
                            var attachIcons: ArrayList<Int> = ArrayList()
                            // attachIcons.add(123)
                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = firebaseUserId
                            userHashMap["Username"] = username
                            userHashMap["EmailId"] = email
                            userHashMap["Status"] = "Offline"
                            userHashMap["Search"] = username.toLowerCase()
                            userHashMap["FaceBook"] = "https://www.facebook.com/"
                            userHashMap["Instagram"] = "https://www.instagram.com/"
                            userHashMap["Website"] = "https://www.google.com/"
                            userHashMap["Groups"] = groups
                            userHashMap["Friends"] = friends
                            userHashMap["Requests"] = requests
                            userHashMap["Requested"] = requested
                            userHashMap["AttachLinks"] = attachLinks
                            userHashMap["AttachIcons"] = attachIcons
                            progressBar.visibility = View.INVISIBLE
                            use?.sendEmailVerification()!!
                                    .addOnSuccessListener {
                                        Toast.makeText(this@RegisterActivity, "Email Verification Link Sent", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(this@RegisterActivity, "Error Occurred: $it", Toast.LENGTH_SHORT).show()
                                    }
                            refUsers.updateChildren(userHashMap)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val intent = Intent(this@RegisterActivity, MainActivity2::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                        }
                        else{
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(this@RegisterActivity, "Error Occurred!! " + task.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }

}