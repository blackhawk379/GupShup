package com.example.chatapplication.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.UserSearchAdapter
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var userRecyclerViewB: RecyclerView
    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: UserSearchAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { return inflater.inflate(R.layout.fragment_search, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userList = ArrayList()
        activity?.let{ adapter = UserSearchAdapter(it, userList) }
        userRecyclerViewB = view?.findViewById(R.id.userRecyclerViewB)!!
        userRecyclerViewB.layoutManager = LinearLayoutManager(activity)
        userRecyclerViewB.adapter = adapter

        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!

        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (i in snapshot.children){
                    val user = i.getValue(Users::class.java)
                    if(currentUser.uid != user?.uid) userList.add(user!!)
                    adapter.notifyDataSetChanged()
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })

        val etSearchText = view?.findViewById<EditText>(R.id.etSearchText)
        etSearchText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().toLowerCase(Locale.ROOT)
                val queryUsers = FirebaseDatabase.getInstance().reference
                        .child("Users").orderByChild("Search")
                        .startAt(str).endAt(str+"\uf8ff")
                queryUsers.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        for (i in snapshot.children){
                            val user = i.getValue(Users::class.java)
                            if(currentUser.uid != user?.uid) userList.add(user!!)
                            adapter.notifyDataSetChanged()
                        }
                    } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                })
            }
        })
    }
    private fun msg(s: String) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show() }
    companion object {}
}