package com.example.chatapplication.MajorActivity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.replace
import androidx.viewpager.widget.ViewPager
import com.example.chatapplication.Fragments.*
import com.example.chatapplication.Model.Users
import com.example.chatapplication.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var fireBaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fireBaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(fireBaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById((R.id.tab_layout))
        val viewPager: ViewPager = findViewById((R.id.view_pager))
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(ChatsFragment(), " Chats")
        viewPagerAdapter.addFragment(SearchFragment(), " Search")
        viewPagerAdapter.addFragment(ProfileFragment(), " Profile")
        viewPagerAdapter.addFragment(GroupFragment(), " Groups")
        viewPagerAdapter.addFragment(FriendRequestFragment(), " Friend Requests")
        viewPagerAdapter.addFragment(FriendsFragment(), " Friends")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        refUsers!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val username = findViewById<TextView>(R.id.user_name)
                    val profile = findViewById<CircleImageView>(R.id.profile_image)
                    username.text = user!!.getUsername()
                    FirebaseStorage.getInstance().reference.child("images/profile$fireBaseUser").downloadUrl
                        .addOnSuccessListener {
                            Picasso.get()
                                .load(it)
                                .placeholder(R.drawable.loading)
                                .error(R.drawable.ic_profile)
                                .into(profile)
                        }
                }
            } override fun onCancelled(error: DatabaseError) { Toast.makeText(this@MainActivity, "Error Occurred: $error", Toast.LENGTH_SHORT).show() }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout ->
            {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }
    internal class ViewPagerAdapter(fragmentManager: FragmentManager):
            FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    {
        private var fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private var titles: ArrayList<String> = ArrayList<String>()

        override fun getItem(position: Int): Fragment { return fragments[position] }
        override fun getCount(): Int { return fragments.size }
        override fun getPageTitle(i: Int): CharSequence? { return titles[i] }

        fun addFragment(fragment: Fragment, title: String)  {
            fragments.add(fragment)
            titles.add(title)
        }
    }
}