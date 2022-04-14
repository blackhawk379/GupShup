package com.example.chatapplication.GroupSpecific

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.MemberAdapter
import com.example.chatapplication.MajorActivity.MainActivity
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupInfoActivity : AppCompatActivity() {
    var refUsers: DatabaseReference? = null
    var refGroups: DatabaseReference? = null
    lateinit var memberRecyclerView: RecyclerView
    private lateinit var memberList: ArrayList<Users>
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)
        memberList = ArrayList()
        adapter = MemberAdapter(this, memberList)
        memberRecyclerView = findViewById(R.id.groupMemberRecyclerView)!!
        memberRecyclerView.layoutManager = LinearLayoutManager(this)
        memberRecyclerView.adapter = adapter
        val btnAddMember = findViewById<Button>(R.id.btnAddMember)
        val progressBar = findViewById<ProgressBar>(R.id.profileProgressbarMember)

        val name = intent.getStringExtra("GroupName")
        val member = intent.getStringExtra("memberId")
        refGroups = name?.let { FirebaseDatabase.getInstance().reference.child("Groups").child(it) }
        if (member != null){
            refGroups!!.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val group: Groups? = snapshot.getValue(Groups::class.java)
                        val members = group?.getMembers()
                        if (member in members!!){
                            val intent = Intent(this@GroupInfoActivity, GroupInfoActivity::class.java)
                            intent.putExtra("GroupName", name)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            members?.add(member)
                            var update: Map<String, ArrayList<String>?>
                            update = mapOf("Members" to members)
                            refGroups!!.updateChildren(update)
                            refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(member)
                            refUsers!!.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user: Users? = snapshot.getValue(Users::class.java)
                                    var groups = user?.getGroups()
                                    if (name in groups!!){
                                        val intent = Intent(this@GroupInfoActivity, GroupInfoActivity::class.java)
                                        intent.putExtra("GroupName", name)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else {
                                        groups?.add(name!!)
                                        var update = mapOf("Groups" to groups)
                                        refUsers!!.updateChildren(update)
                                    }
                                }
                            })
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        val groupname = findViewById<TextView>(R.id.txtGroupInfo)
        val groupDescription = findViewById<TextView>(R.id.txtGroupDescription)
        refGroups!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val group: Groups? = snapshot.getValue(Groups::class.java)
                    groupname.text = name
                    groupDescription.text = group?.getDescription().toString()
                    memberList.clear()
                    for (i in group?.getMembers()!!){
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(i)
                        refUsers!!.addValueEventListener(object: ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user: Users? = snapshot.getValue(Users::class.java)
                                memberList.add(user!!)
                                adapter.notifyDataSetChanged()
                            }

                        })
                    }
                    progressBar.visibility = View.GONE
                    val mAuth = FirebaseAuth.getInstance()
                    if (mAuth.currentUser?.uid==group.getAdmin()){
                        btnAddMember.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    btnAddMember.setOnClickListener {
                        if (mAuth.currentUser?.uid==group.getAdmin()) {
                            val intent = Intent(this@GroupInfoActivity, SearchActivity::class.java)
                            intent.putExtra("name", name)
                            startActivity(intent)
                            finish()
                        }
                        else Toast.makeText(this@GroupInfoActivity, "Only Admin can add members", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val toolbar = findViewById<Toolbar>(R.id.toolbar_group_info)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this@GroupInfoActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }
}