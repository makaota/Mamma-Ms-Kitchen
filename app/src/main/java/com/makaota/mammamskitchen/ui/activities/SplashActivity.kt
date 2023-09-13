package com.makaota.mammamskitchen.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.makaota.mammamskitchen.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else
        {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Adding the handler to after the a task after some delay.
        // It is deprecated in the API level 30.
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                // Launch the Main Activity
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish() // Call this when your activity is done and should be closed.
            },
            3000
        ) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.

        // END
    }
}