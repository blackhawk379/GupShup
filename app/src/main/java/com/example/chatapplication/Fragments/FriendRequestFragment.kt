package com.example.chatapplication.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.GroupAdapter
import com.example.chatapplication.Adapter.RequestAdapter
import com.example.chatapplication.Adapter.UserSearchAdapter
import com.example.chatapplication.GroupSpecific.AddGroup
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FriendRequestFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var requestRecyclerView: RecyclerView
    private lateinit var requestList: ArrayList<Users>
    private lateinit var adapter: UserSearchAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var refRequests: DatabaseReference

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
    ): View? { return inflater.inflate(R.layout.fragment_friend_request, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestList = ArrayList()
        adapter = UserSearchAdapter(requireActivity(), requestList)
        requestRecyclerView = view?.findViewById(R.id.requestRecyclerView)!!
        requestRecyclerView.layoutManager = LinearLayoutManager(activity)
        requestRecyclerView.adapter = adapter

        val mAuth = FirebaseAuth.getInstance().currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth)
        refUsers.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                requestList.clear()
                for (i in user?.getRequests()!!){
                    if (i == "ABC") continue
                    refRequests = FirebaseDatabase.getInstance().reference.child("Users").child(i)
                    refRequests.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var req: Users? = snapshot.getValue(Users::class.java)
                            requestList.add(req!!)
                            adapter.notifyDataSetChanged()
                        } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                    })
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })

    }
    private fun msg(s: String) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show() }

    companion object {}
}