package com.example.chatapplication.Fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.chatapplication.Model.Message
import com.example.chatapplication.UserSpecific.EditProfileInfo
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri as Uri1

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var refUsers: DatabaseReference? = null
    var fireBaseUser: FirebaseUser? = null
    private lateinit var mDbref: DatabaseReference
    private lateinit var imageUri: Uri1


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mDbref = FirebaseDatabase.getInstance().reference
        fireBaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)
        refUsers!!.addValueEventListener(object: ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())  {
                    val user: Users? = snapshot.getValue(Users::class.java)

                    val username = view?.findViewById<TextView>(R.id.txtProfile)
                    val imgEditCover = view?.findViewById<CircleImageView>(R.id.imgEditCover)
                    val imgEditProfile = view?.findViewById<CircleImageView>(R.id.imgEditProfile)
                    val imgCover = view?.findViewById<ImageView>(R.id.imgCover)
                    val imgProfile = view?.findViewById<ImageView>(R.id.imgProfileFragment)
                    val progressBar = view?.findViewById<ProgressBar>(R.id.profileProgressbar)
                    val imgUsername = view?.findViewById<CircleImageView>(R.id.imgEditProfiletxt)
                    val imgInstagram = view?.findViewById<CircleImageView>(R.id.imgEditInsta)
                    val imgFacebook = view?.findViewById<CircleImageView>(R.id.imgEditFaceBook)
                    val imgWebsite = view?.findViewById<CircleImageView>(R.id.imgEditWebsite)
                    val imgProfileInsta = view?.findViewById<CircleImageView>(R.id.imgProfileInsta)
                    val imgProfileFaceBook = view?.findViewById<CircleImageView>(R.id.imgProfileFaceBook)
                    val imgProfileWebsite = view?.findViewById<CircleImageView>(R.id.imgProfileWebsite)

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
                        .addOnFailureListener { println("Download Error: $it") }
                    FirebaseStorage.getInstance().reference.child("images/profile${fireBaseUser!!.uid}").downloadUrl
                        .addOnSuccessListener {
                            println("Download Successful")
                            Picasso.get()
                                .load(it)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.ic_profile)
                                .into(imgProfile)
                        }
                        .addOnFailureListener { println("Download Error: $it") }
                    progressBar?.visibility = View.INVISIBLE

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
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })
    }

    private fun selectImage(i: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgCover = view?.findViewById<ImageView>(R.id.imgCover)
        val imgProfile = view?.findViewById<ImageView>(R.id.imgProfileFragment)
        var progressBar = view?.findViewById<ProgressBar>(R.id.profileProgressbar)
        progressBar?.visibility = View.VISIBLE
        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            FirebaseStorage.getInstance()
                .getReference("images/cover${fireBaseUser!!.uid}")
                .putFile(imageUri)
                .addOnSuccessListener {
                    msg("Upload Successful")
                    imgCover?.setImageURI(imageUri)
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
                        progressBar?.visibility = View.INVISIBLE
                    }
                .addOnFailureListener { msg("Upload Error: $it") }
            }
        }
    private fun intent(key: String){
        val intent = Intent(activity, EditProfileInfo::class.java)
        intent.putExtra("key", key)
        startActivity(intent)
    }
    private fun browser(uri: String){
        val browserIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
        startActivity(browserIntent)
    }
    private fun msg(msg: String) = Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_profile, container, false) }

    companion object {}
}