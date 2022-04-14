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
import com.example.chatapplication.Adapter.GroupAdapter
import com.example.chatapplication.GroupSpecific.AddGroup
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.example.chatapplication.R.id.btnNewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var groupRecyclerView: RecyclerView
    private lateinit var groupList: ArrayList<Groups>
    private lateinit var adapter: GroupAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var refGroups: DatabaseReference

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
    ): View? { return inflater.inflate(R.layout.fragment_group, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnNewGroup = view?.findViewById<Button>(btnNewGroup)
        btnNewGroup?.setOnClickListener {
            val intent = Intent(activity, AddGroup::class.java)
            startActivity(intent)
        }
        groupList = ArrayList()
        activity?.let{ adapter = GroupAdapter(it, groupList) }
        groupRecyclerView = view?.findViewById(R.id.groupRecyclerView)!!
        groupRecyclerView.layoutManager = LinearLayoutManager(activity)
        groupRecyclerView.adapter = adapter
        val uidd = FirebaseAuth.getInstance().currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(uidd)
        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)
                var groups = user?.getGroups()
                groupList.clear()
                if ("ABC" in groups!!) groups?.remove("ABC")
                for (i in groups!!){
                    refGroups = FirebaseDatabase.getInstance().reference.child("Groups").child(i)
                    refGroups!!.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var group: Groups? = snapshot.getValue(Groups::class.java)
                            groupList.add(group!!)
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