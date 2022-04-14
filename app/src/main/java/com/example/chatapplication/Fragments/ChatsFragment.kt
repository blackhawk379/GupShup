package com.example.chatapplication.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.Adapter.UserAdapter
import com.example.chatapplication.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

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
    ): View? { return inflater.inflate(R.layout.fragment_chats, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList = ArrayList()
        activity?.let{ adapter = UserAdapter(it, userList) }
        userRecyclerView = view.findViewById(R.id.userRecyclerView)!!
        userRecyclerView.layoutManager = LinearLayoutManager(activity)
        userRecyclerView.adapter = adapter
        refUsers = FirebaseDatabase.getInstance().reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        refUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (i in snapshot.children){
                    val currentUser = i.getValue(Users::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) userList.add(currentUser!!)
                    adapter.notifyDataSetChanged()
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })
    }
    private fun msg(s: String) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show() }

    companion object {}
}