package com.example.chatapplication.GroupSpecific

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.MemberSearchAdapter
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {

    lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: MemberSearchAdapter
    private lateinit var refUsers: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        userList = ArrayList()
        val name = intent.getStringExtra("name")
        adapter = MemberSearchAdapter(this, userList, name!!)
        userRecyclerView = findViewById(R.id.userRecyclerViewc)!!
        val etSearchText = findViewById<EditText>(R.id.etSearchTextb)
        val btnAddMemberSearch = findViewById<Button>(R.id.btnAddMemberSearch)
        val groupId = intent.getStringExtra("groupId")

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        mAuth = FirebaseAuth.getInstance()
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)
        refUsers!!.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val user: Users? = snapshot.getValue(Users::class.java)
                for (i in user!!.getFriends()){
                    if (i!="ABC") {
                        val refFriends = FirebaseDatabase.getInstance().reference.child("Users").child(i)
                        refFriends!!.addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var frnd: Users? = snapshot.getValue(Users::class.java)
                                if (frnd?.uid!=mAuth.currentUser!!.uid) userList.add(frnd!!)
                            }
                        })
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
        etSearchText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().toLowerCase()
                val queryUsers = FirebaseDatabase.getInstance().reference
                    .child("Users").orderByChild("Search")
                    .startAt(str).endAt(str+"\uf8ff")

                queryUsers.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()
                        for (i in snapshot.children){
                            val currentUser = i.getValue(Users::class.java)
                            if(mAuth.currentUser?.uid != currentUser?.uid) userList.add(currentUser!!)
                            adapter.notifyDataSetChanged()
                        }
                    }

                })
            }

        })
    }
}