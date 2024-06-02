package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mymaptestapp.adapters.AdapterStartPointList
import com.example.mymaptestapp.databinding.ChooseStartPointActivityBinding
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.NewRouteUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChooseStartPointActivity : AppCompatActivity() {

    private lateinit var binding: ChooseStartPointActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChooseStartPointActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        checkUser()
        initPoints()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            NewRouteUtils.setStartPoint(null)
        }

        binding.nextButton.setOnClickListener {
            startActivity(Intent(this, ChooseRoutePointsActivity::class.java))
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
            val result = JwtTokenUtils.checkTokens(this@ChooseStartPointActivity)

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@ChooseStartPointActivity, LoginActivity::class.java))
                }
            }
        }
    }

    /**
     * Загрузка списка точек на карте
     */
    private fun initPoints() {
        binding.startPointList.setHasFixedSize(true)
        binding.startPointList.setItemViewCacheSize(10)

        val adapter = AdapterStartPointList(this@ChooseStartPointActivity, DeliveryPointUtils.getPoints()) {
            binding.nextButton.visibility = View.VISIBLE
        }

        binding.startPointList.adapter = adapter

        binding.searchEt.addTextChangedListener { input ->
            val filteredList = DeliveryPointUtils.getPoints()!!.filter { point -> point.address.lowercase().contains(input.toString().lowercase()) }
            adapter.setFilteredList(filteredList)
        }
    }
}