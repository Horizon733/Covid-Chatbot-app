package com.example.covidapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH:Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val textView = findViewById<TextView>(R.id.app_name)
        val imageView = findViewById<ImageView>(R.id.image_view)
        fadeOutAndHideImage(imageView,textView)

    }
    private fun fadeOutAndHideImage(img: ImageView,textView: TextView) {
        val fadeOut: Animation = AlphaAnimation(1F,0F)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = SPLASH_DISPLAY_LENGTH
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                img.visibility = View.GONE
                textView.visibility = View.GONE
                    Handler().postDelayed(Runnable {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }, SPLASH_DISPLAY_LENGTH.toLong())
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationStart(animation: Animation) {}
        })
        img.startAnimation(fadeOut)
        textView.startAnimation(fadeOut)
    }
}