package com.example.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.Adapter.TestAdapter
import com.example.chatapplication.Adapter.UserAdapter
import com.example.chatapplication.Model.Users

class Test : AppCompatActivity() {

    lateinit var testRecyclerView: RecyclerView
    private lateinit var testList: ArrayList<String>
    private lateinit var adapter: TestAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        testList = ArrayList()
        testList.add("insta")
        testList.add("website")
        adapter = TestAdapter(this@Test, testList)
        testRecyclerView = findViewById(R.id.testRecyclerView)!!
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        testRecyclerView.layoutManager = layoutManager
        testRecyclerView.adapter = adapter
    }
}