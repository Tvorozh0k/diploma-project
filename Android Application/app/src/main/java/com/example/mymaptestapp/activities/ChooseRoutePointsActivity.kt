package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mymaptestapp.adapters.AdapterRoutePointsList
import com.example.mymaptestapp.databinding.ChooseRoutePointsActivityBinding
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.NewRouteUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseRoutePointsActivity : AppCompatActivity() {

    private lateinit var binding: ChooseRoutePointsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChooseRoutePointsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        checkUser()
        initPoints()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            NewRouteUtils.clearRoutePoints()
        }

        binding.nextButton.setOnClickListener {
            startActivity(Intent(this, GenerateRouteActivity::class.java))
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
            val result = JwtTokenUtils.checkTokens(this@ChooseRoutePointsActivity)

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@ChooseRoutePointsActivity, LoginActivity::class.java))
                }
            }
        }
    }

    /**
     * Загрузка списка точек на карте
     */
    private fun initPoints() {
        binding.routePointsList.setHasFixedSize(true)
        binding.routePointsList.setItemViewCacheSize(10)

        val points = DeliveryPointUtils.getPoints()!!.filter { point -> point.id != NewRouteUtils.getStartPoint() }

        val adapter = AdapterRoutePointsList(this@ChooseRoutePointsActivity, points) {
            binding.nextButton.visibility = if (NewRouteUtils.isRoutePointsEmpty()) View.GONE else View.VISIBLE
        }

        binding.routePointsList.adapter = adapter

        binding.searchEt.addTextChangedListener { input ->
            val filteredList = points.filter { point -> point.address.lowercase().contains(input.toString().lowercase()) }
            adapter.setFilteredList(filteredList)
        }
    }
}