package com.example.chatapplication.MajorActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        var user = FirebaseAuth.getInstance().currentUser
        if (user!!.isEmailVerified){
            val intent = Intent(this@MainActivity3, MainActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(this@MainActivity3, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}