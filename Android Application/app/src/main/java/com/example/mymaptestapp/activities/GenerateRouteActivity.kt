package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.mymaptestapp.adapters.AdapterRouteInfo
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.NewRouteUtils
import com.example.mymaptestapp.databinding.ActivityGenerateRouteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenerateRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerateRouteBinding
    private var routeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        checkUser()

        initSolution()

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapRouteActivity::class.java)
            intent.putExtra("routeId", routeId)
            startActivity(intent)
        }
    }

    /**
     * Настраиваем строку состояния
     */
    private fun initStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * Вспомогательная функция для настройки строки состояния
     */
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

    /**
     * Проверяем JWT-токены на срок истечения
     */
    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@GenerateRouteActivity)

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@GenerateRouteActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initSolution() {
        binding.routeInfoList.setHasFixedSize(true)
        binding.routeInfoList.setItemViewCacheSize(10)

        CoroutineScope(Dispatchers.IO).launch {
            val solution = NewRouteUtils.getSolution(this@GenerateRouteActivity) { value, id ->
                routeId = id
                binding.distance.text = "%.1f км".format(value / 1000.0)
            }

            NewRouteUtils.setOption(null)
            NewRouteUtils.setStartPoint(null)
            NewRouteUtils.clearRoutePoints()

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.message.visibility = View.GONE

                binding.scrollRoute.visibility = View.VISIBLE
                binding.mapButton.visibility = View.VISIBLE
                binding.distance.visibility = View.VISIBLE

                val adapter = AdapterRouteInfo(this@GenerateRouteActivity, solution)
                binding.routeInfoList.adapter = adapter
            }
        }
    }
}