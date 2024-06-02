package com.example.mymaptestapp.activities

import com.example.mymaptestapp.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mymaptestapp.fragments.AccountFragment
import com.example.mymaptestapp.fragments.NewRouteFragment
import com.example.mymaptestapp.fragments.RouteHistoryFragment
import com.example.mymaptestapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        replaceFragment(NewRouteFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newRoute -> replaceFragment(NewRouteFragment())
                R.id.history -> replaceFragment(RouteHistoryFragment())
                R.id.account -> replaceFragment(AccountFragment())
            }

            true
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
     * Отображаем новый фрагмент
     */
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}