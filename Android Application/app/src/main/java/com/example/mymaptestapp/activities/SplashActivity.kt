package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.utils.UserDataUtils
import com.example.mymaptestapp.databinding.ActivitySplashBinding
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.api.utils.UserRouteUtils
import com.example.mymaptestapp.utils.JwtTokenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()

        val images = arrayOf(
            R.drawable.splash_back_1,
            R.drawable.splash_back_2,
            R.drawable.splash_back_3,
            R.drawable.splash_back_4
        )

        val imageView : ImageView = binding.background
        imageView.setImageResource(images.random())

        checkUser()
    }

    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@SplashActivity)

            if (result) {
                UserDataUtils.setUserData(this@SplashActivity)
                DeliveryPointUtils.setPoints(this@SplashActivity)
                UserRouteUtils.setRoutes(this@SplashActivity)
            } else {
                delay(1000)
            }

            withContext(Dispatchers.Main) {
                if (result) {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                } else {

                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
            }

            finish()
        }
    }

    private fun initStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}