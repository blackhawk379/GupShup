package com.example.chatapplication.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.UserAdapter
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FriendsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendList: ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var refFriends: DatabaseReference
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
    ): View? { return inflater.inflate(R.layout.fragment_friends, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendList = ArrayList()
        activity?.let{ adapter = UserAdapter(it, friendList) }
        friendRecyclerView = view.findViewById(R.id.friendRecyclerView)!!
        friendRecyclerView.layoutManager = LinearLayoutManager(activity)
        friendRecyclerView.adapter = adapter
        val uidd = FirebaseAuth.getInstance().currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(uidd)
        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                friendList.clear()
                var friends = user?.getFriends()
                if ("ABC" in friends!!) friends.remove("ABC")
                for (i in friends){
                    refFriends = FirebaseDatabase.getInstance().reference.child("Users").child(i)
                    refFriends.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var frnd: Users? = snapshot.getValue(Users::class.java)
                            if (frnd?.uid!=uidd) {
                                friendList.add(frnd!!)
                                adapter.notifyDataSetChanged()
                            }
                        } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
                    })
                }
            } override fun onCancelled(error: DatabaseError) { msg("Error Occurred: $error") }
        })
    }

    private fun msg(s: String) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show() }

    companion object {}
}