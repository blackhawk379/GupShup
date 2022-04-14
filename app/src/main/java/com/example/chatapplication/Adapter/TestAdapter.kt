package com.example.chatapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.GroupSpecific.GroupChatActivity
import com.example.chatapplication.GroupSpecific.GroupInfoActivity
import com.example.chatapplication.Model.Groups
import com.example.chatapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class TestAdapter (val context: Context, val testList: ArrayList<String>):
    RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.test_layout, parent, false)
        return TestViewHolder(view)
    }


    override fun getItemCount(): Int {
        return testList.size
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        if (testList[position]=="insta") holder.img.setImageResource(R.drawable.insta)
        if (testList[position]=="fb") holder.img.setImageResource(R.drawable.fb)
        if (testList[position]=="website") holder.img.setImageResource(R.drawable.website)
    }
    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: CircleImageView = itemView.findViewById(R.id.imgTest)
    }
}