package com.example.chatapplication.MajorActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.UserSpecific.EditProfileInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MainActivity2 : AppCompatActivity() {
    var refUsers: DatabaseReference? = null
    var fireBaseUser: FirebaseUser? = null
    lateinit var imageUri: Uri
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        fireBaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)
        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val username = findViewById<TextView>(R.id.txtProfile1)
                    val imgEditCover = findViewById<CircleImageView>(R.id.imgEditCover1)
                    val imgEditProfile = findViewById<CircleImageView>(R.id.imgEditProfile1)
                    val imgCover = findViewById<ImageView>(R.id.imgCover1)
                    val imgProfile = findViewById<ImageView>(R.id.imgProfileFragment1)
                    val imgUsername = findViewById<CircleImageView>(R.id.imgEditProfiletxt1)
                    val imgInstagram = findViewById<CircleImageView>(R.id.imgEditInsta1)
                    val imgFacebook = findViewById<CircleImageView>(R.id.imgEditFaceBook1)
                    val imgWebsite = findViewById<CircleImageView>(R.id.imgEditWebsite1)
                    val progressBar = findViewById<ProgressBar>(R.id.profileProgressbar1)

                    progressBar?.visibility = View.VISIBLE
                    username?.text = user!!.getUsername()
                    FirebaseStorage.getInstance().reference.child("images/cover${fireBaseUser!!.uid}").downloadUrl
                        .addOnSuccessListener {
                            println("Download Successful")
                            Picasso.get()
                                .load(it)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.ic_cover)
                                .into(imgCover)
                        }
                    FirebaseStorage.getInstance().reference.child("images/profile${fireBaseUser!!.uid}").downloadUrl
                        .addOnSuccessListener {
                            println("Download Successful")
                            Picasso.get()
                                .load(it)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.ic_profile)
                                .into(imgProfile)
                        }
                    progressBar?.visibility = View.INVISIBLE

                    val imgProfileInsta = findViewById<CircleImageView>(R.id.imgProfileInsta1)
                    val imgProfileFaceBook = findViewById<CircleImageView>(R.id.imgProfileFaceBook1)
                    val imgProfileWebsite = findViewById<CircleImageView>(R.id.imgProfileWebsite1)

                    imgProfileInsta?.setOnClickListener { browser(user.getInstagram()!!) }
                    imgProfileFaceBook?.setOnClickListener { browser(user.getFaceBook()!!) }
                    imgProfileWebsite?.setOnClickListener { browser(user.getWebsite()!!) }
                    imgEditCover?.setOnClickListener { selectImage(100) }
                    imgEditProfile?.setOnClickListener { selectImage(200) }
                    imgUsername?.setOnClickListener{ intent("1") }
                    imgInstagram?.setOnClickListener{ intent("2") }
                    imgFacebook?.setOnClickListener{ intent("3") }
                    imgWebsite?.setOnClickListener{ intent("4") }
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error")  }
        })
        val toolbar: Toolbar = findViewById(R.id.toolbar_create)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Establish Your Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MainActivity2, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        var btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val intent = Intent(this@MainActivity2, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun selectImage(i: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, i)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgCover = findViewById<ImageView>(R.id.imgCover1)
        val imgProfile = findViewById<ImageView>(R.id.imgProfileFragment1)
        var progressBar = findViewById<ProgressBar>(R.id.profileProgressbar1)
        progressBar?.visibility = View.VISIBLE
        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            FirebaseStorage.getInstance()
                .getReference("images/cover${fireBaseUser!!.uid}")
                .putFile(imageUri)
                .addOnSuccessListener {
                    msg("Upload Successful")
                    imgCover?.setImageURI(imageUri)
                    imgCover.bringToFront()
                    progressBar?.visibility = View.INVISIBLE
                }
                .addOnFailureListener { msg("Upload Error: $it") }
        }
        if(requestCode == 200 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            FirebaseStorage.getInstance()
                .getReference("images/profile${fireBaseUser!!.uid}")
                .putFile(imageUri)
                .addOnSuccessListener {
                    msg("Upload Successful")
                    imgProfile?.setImageURI(imageUri)
                    imgProfile.bringToFront()
                    progressBar?.visibility = View.INVISIBLE
                }
                .addOnFailureListener { msg("Upload Error: $it") }
        }
    }
    private fun intent(key: String){
        val intent = Intent(this@MainActivity2, EditProfileInfo::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }
    private fun browser(uri: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(browserIntent)
    }
    private fun msg(msg: String) = Toast.makeText(this@MainActivity2, msg, Toast.LENGTH_SHORT).show()
}