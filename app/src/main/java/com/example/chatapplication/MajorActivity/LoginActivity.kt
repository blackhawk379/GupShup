package com.example.chatapplication.MajorActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener {
            loginUser()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loginUser() {
        val emailLogin = findViewById<EditText>(R.id.email_login)
        val passwordLogin = findViewById<EditText>(R.id.password_login)

        val email: String = emailLogin.text.toString()
        val password: String = passwordLogin.text.toString()

        when{
            email == "" -> Toast.makeText(this@LoginActivity, "Please Enter an Email ID", Toast.LENGTH_LONG).show()
            password == "" -> Toast.makeText(this@LoginActivity, "Please Enter a Password", Toast.LENGTH_LONG).show()
            else -> {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (mAuth.currentUser!!.isEmailVerified) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                                startActivity(intent)
                                finish()
                            }
                            else {
                                val mDialogView = View.inflate(this@LoginActivity, R.layout.dialog_box, null)
                                val mBuilder = AlertDialog.Builder(this@LoginActivity).setView(mDialogView)
                                val mAlertDialog = mBuilder.show()
                                mDialogView.setBackgroundDrawable(getDrawable(R.drawable.myshape))
                                mDialogView.findViewById<Button>(R.id.btnSendAgain).setOnClickListener{
                                    val user = mAuth.currentUser
                                    user?.sendEmailVerification()!!
                                            .addOnSuccessListener {
                                                Toast.makeText(this@LoginActivity, "Verification Link Sent", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener{
                                                Toast.makeText(this@LoginActivity, "Error Occurred $it", Toast.LENGTH_SHORT).show()
                                            }
                                    mAlertDialog.dismiss()
                                }
                                mDialogView.findViewById<Button>(R.id.btnCancelVerify).setOnClickListener{
                                    mAlertDialog.dismiss()
                                }
                            }

                        }
                        else Toast.makeText(this@LoginActivity, "Error Occurred " + task.exception!!.message, Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}