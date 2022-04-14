package com.example.chatapplication.MajorActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.chatapplication.R

class SplashScreenActivity2 : AppCompatActivity() {
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen2)
        val appTitle: TextView = findViewById(R.id.txtAppTitle2)
        val companyTitle: TextView = findViewById(R.id.txtCompanyTitle2)
        val from: TextView = findViewById(R.id.txtFrom2)
        val sideSlide = AnimationUtils.loadAnimation(this,
            R.anim.in_place
        )
        val downSlide = AnimationUtils.loadAnimation(this,
            R.anim.down_slide
        )
        val inPlace = AnimationUtils.loadAnimation(this,
            R.anim.in_place
        )
        appTitle.startAnimation(sideSlide)
        from.startAnimation(inPlace)
        companyTitle.startAnimation(downSlide)
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this@SplashScreenActivity2, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}